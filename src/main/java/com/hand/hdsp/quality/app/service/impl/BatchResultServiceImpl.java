package com.hand.hdsp.quality.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchResultService;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.PlanConstant.ExceptionParam;
import com.hand.hdsp.quality.infra.feign.ExecutionFlowFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.core.util.ResponseUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private final BatchResultRepository batchResultRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public BatchResultServiceImpl(ExecutionFlowFeign executionFlowFeign,
                                  BatchResultMapper batchResultMapper,
                                  BatchResultItemMapper batchResultItemMapper,
                                  BatchResultBaseRepository batchResultBaseRepository, BatchResultRepository batchResultRepository) {
        this.executionFlowFeign = executionFlowFeign;
        this.batchResultMapper = batchResultMapper;
        this.batchResultItemMapper = batchResultItemMapper;
        this.batchResultBaseRepository = batchResultBaseRepository;
        this.batchResultRepository = batchResultRepository;
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
            List<String> standardJsonList = batchResultItemMapper.dataStandardWaringLevelVO(dto);
            if (CollectionUtils.isNotEmpty(standardJsonList)) {
                batchResultDTO.setDataStandardCount(standardJsonList.size() + Optional.ofNullable(batchResultDTO.getDataStandardCount()).orElse(0L));
                standardJsonList.forEach(standardWarningLevel -> {
                    List<WarningLevelVO> warningLevelVOS = JsonUtils.json2WarningLevelVO(standardWarningLevel);
                    if (CollectionUtils.isNotEmpty(warningLevelVOS)) {
                        batchResultDTO.setExceptionDataStandardCount(Optional.ofNullable(batchResultDTO.getExceptionDataStandardCount()).orElse(0L) + 1L);
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
        //返回方案下的所有告警等级以及对应的数量
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

    @Override
    public Page<BatchResultBaseDTO> listExceptionDetailHead(Long resultId, Long tenantId, PageRequest pageRequest) {
        //根据resultId查询方案Id
        BatchResultDTO dto = batchResultRepository.selectDTOByPrimaryKey(resultId);
        //查询方案下所有resultBase
        if (Objects.isNull(dto)) {
            throw new CommonException(ErrorCode.BATCH_RESULT_NOT_EXIST);
        }
        Page<BatchResultBaseDTO> page = batchResultBaseRepository.pageAndSortDTO(pageRequest, BatchResultBaseDTO.builder().resultId(resultId).tenantId(tenantId).build());
        List<BatchResultBaseDTO> batchResultBaseDTOList = page.getContent();
        if (CollectionUtils.isNotEmpty(batchResultBaseDTOList)) {
            for (BatchResultBaseDTO batchResultBaseDTO : batchResultBaseDTOList) {
                //获取base下所有校验项的告警等级Json
                List<String> warningLevelJsonList = batchResultItemMapper.selectWaringLevelJson(batchResultBaseDTO);
                //将所有告警等级Json转换合并成集合
                List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
                warningLevelJsonList.forEach(warningLevelJson -> warningLevelVOList.addAll(JsonUtils.json2WarningLevelVO(warningLevelJson)));
                //合并处理每个检验项的告警等级
                Map<String, Long> collect = warningLevelVOList.stream()
                        .collect(
                                Collectors.toMap(WarningLevelVO::getWarningLevel,
                                        WarningLevelVO::getLevelCount,
                                        Long::sum));
                //定义告警结果对象集合
                List<ResultWaringVO> resultWaringVOList = new ArrayList<>();
                collect.forEach((k, v) -> {
                    ResultWaringVO resultWaringVO = ResultWaringVO.builder()
                            .warningLevel(k)
                            .countSum(v)
                            .build();
                    resultWaringVOList.add(resultWaringVO);
                });
                //设置到每个base中
                batchResultBaseDTO.setResultWaringVOList(resultWaringVOList);
                if (Objects.isNull(batchResultBaseDTO.getDataCount())) {
                    batchResultBaseDTO.setDataCount(0L);
                }
                page.setContent(batchResultBaseDTOList);
            }
        }
        return page;
    }

    @Override
    public Page<Map<String, Object>> listExceptionDetail(ExceptionDataDTO exceptionDataDTO, PageRequest pageRequest) {
        if (Objects.isNull(exceptionDataDTO.getPlanBaseId())) {
            throw new CommonException(ErrorCode.PLAN_BASE_ID_IS_EMPTY);
        }
        //查看base的异常数据 目前只有字段级有异常数据
        Object json = redisTemplate.opsForHash().get(String.format("%s:%d", PlanConstant.CACHE_BUCKET_EXCEPTION,
                exceptionDataDTO.getPlanBaseId()), PlanConstant.ResultRuleType.FIELD);
        List<Map<String, Object>> result = (List<Map<String, Object>>) JSONArray.parse(String.valueOf(json));
        if (Strings.isNotEmpty(exceptionDataDTO.getRuleName())) {
            result = result.stream().filter(map -> {
                String ruleName = String.valueOf(map.get("$ruleName"));
                return ruleName.contains(exceptionDataDTO.getRuleName());
            }).collect(Collectors.toList());
        }
        if (Strings.isNotEmpty(exceptionDataDTO.getWarningLevel())) {
            result = result.stream().filter(map -> {
                String warningLevel =Optional.ofNullable(String.valueOf(map.get(ExceptionParam.WARNING_LEVEL)))
                        .orElse("");
                List<String> list = Arrays.asList(warningLevel.split(","));
                if (CollectionUtils.isEmpty(list)){
                    throw new CommonException(ErrorCode.EXCEPTION_PARAM_ERROR);
                }
                return list.contains(exceptionDataDTO.getWarningLevel());
            }).collect(Collectors.toList());
        }
        int start = pageRequest.getPage() * pageRequest.getSize();
        int end=start+pageRequest.getSize();
        if(start>end){
            throw new CommonException(ErrorCode.PAGE_ERROR);
        }
        List<Map<String,Object>> content=new ArrayList<>();
        for (int i=start;i<end;i++){
            if(result.size()-1>=i){
                content.add(result.get(i));
            }else{
                break;
            }
        }
        return new Page<>(content, new PageInfo(pageRequest.getPage(), pageRequest.getSize()), result.size());
    }

}
