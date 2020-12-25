package com.hand.hdsp.quality.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.util.ResponseUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.BatchResultMarkDTO;
import com.hand.hdsp.quality.api.dto.ResultObjDTO;
import com.hand.hdsp.quality.app.service.BatchResultService;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import com.hand.hdsp.quality.infra.feign.ExecutionFlowFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;

/**
 * <p>批数据方案结果表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class BatchResultServiceImpl implements BatchResultService {

    private final ExecutionFlowFeign executionFlowFeign;
    private final BatchResultMapper batchResultMapper;
    private final BatchResultItemMapper batchResultItemMapper;
    private final BatchResultBaseRepository batchResultBaseRepository;

    public BatchResultServiceImpl(ExecutionFlowFeign executionFlowFeign,
                                  BatchResultMapper batchResultMapper,
                                  BatchResultItemMapper batchResultItemMapper,
                                  BatchResultBaseRepository batchResultBaseRepository) {
        this.executionFlowFeign = executionFlowFeign;
        this.batchResultMapper = batchResultMapper;
        this.batchResultItemMapper = batchResultItemMapper;
        this.batchResultBaseRepository = batchResultBaseRepository;
    }


    @Override
    public ResultObjDTO showLog(Long tenantId, int execId, String jobId) {
        ResponseEntity<String> result = executionFlowFeign.getJobLog(tenantId, execId, jobId);
        if (ResponseUtils.isFailed(result)) {
            // 获取异常信息
            ExceptionResponse response = ResponseUtils.getResponse(result, ExceptionResponse.class);
            throw new CommonException(response.getMessage());
        }
        return ResponseUtils.getResponse(result, ResultObjDTO.class);
    }

    @Override
    public BatchResultDTO listResultDetail(BatchResultDTO batchResultDTO) {
        List<BatchResultMarkDTO> batchResultMarkDTOS = batchResultMapper.listResultDetail(batchResultDTO);
        batchResultDTO.setBatchResultMarkDTOList(batchResultMarkDTOS.stream()
                .filter(Objects::nonNull).collect(Collectors.toList()));
        List<BatchResultBaseDTO> batchResultBaseDTOList = batchResultBaseRepository.selectDTOByCondition(
                Condition.builder(BatchResultBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchResultBase.FIELD_RESULT_ID, batchResultDTO.getResultId())
                                .andEqualTo(BatchResultBase.FIELD_TENANT_ID, batchResultDTO.getTenantId()))
                        .build());

        //将所有告警等级Json转换合并成集合
        List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
        List<ResultWaringVO> resultWaringVOList = new ArrayList<>();
        for (BatchResultBaseDTO dto : batchResultBaseDTOList) {
            //获取base下所有校验项的告警等级Json
            List<String> warningLevelJsonList = batchResultItemMapper.selectWaringLevelJson(dto);
            //将所有告警等级Json转换合并成集合
            warningLevelJsonList.forEach(warningLevelJson -> warningLevelVOList.addAll(JsonUtils.json2WarningLevelVO(warningLevelJson)));

            //数据标准告警JSON集合
            List<String> standardJsonList= batchResultItemMapper.dataStandardWaringLevelVO(dto);
            if(CollectionUtils.isNotEmpty(standardJsonList)){
                batchResultDTO.setDataStandardCount(standardJsonList.size() + Optional.ofNullable(batchResultDTO.getDataStandardCount()).orElse(0L));
                standardJsonList.forEach(standardWarningLevel->{
                    List<WarningLevelVO> warningLevelVOS = JsonUtils.json2WarningLevelVO(standardWarningLevel);
                    if(CollectionUtils.isNotEmpty(warningLevelVOS)){
                        batchResultDTO.setExceptionDataStandardCount(Optional.ofNullable(batchResultDTO.getExceptionDataStandardCount()).orElse(0L)+1L);
                    }
                });
            }
            batchResultDTO.setRuleCount(dto.getRuleCount() +
                    Optional.ofNullable(batchResultDTO.getRuleCount()).orElse(0L));
            batchResultDTO.setExceptionRuleCount(dto.getExceptionRuleCount() +
                    Optional.ofNullable(batchResultDTO.getExceptionRuleCount()).orElse(0L));
            batchResultDTO.setCheckItemCount(dto.getCheckItemCount() +
                    Optional.ofNullable(batchResultDTO.getCheckItemCount()).orElse(0L));
            batchResultDTO.setExceptionCheckItemCount(dto.getExceptionCheckItemCount() +
                    Optional.ofNullable(batchResultDTO.getExceptionCheckItemCount()).orElse(0L));
        }
        //合并处理每个检验项的告警等级
        Map<String, Long> collect = warningLevelVOList.stream()
                .collect(
                        Collectors.toMap(WarningLevelVO::getWarningLevel,
                                WarningLevelVO::getLevelCount,
                                Long::sum));
        //返回base下的所有告警等级以及对应的数量
        collect.forEach((k, v) -> {
            ResultWaringVO resultWaringVO = ResultWaringVO.builder()
                    .warningLevel(k)
                    .countSum(v)
                    .build();
            resultWaringVOList.add(resultWaringVO);
        });
        batchResultDTO.setResultWaringVOList(resultWaringVOList);
        return batchResultDTO;
    }
}
