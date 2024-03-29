package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchResultBaseDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.domain.repository.BatchResultBaseRepository;
import org.xdsp.quality.infra.mapper.BatchResultItemMapper;
import org.xdsp.quality.infra.util.JsonUtils;
import org.xdsp.quality.infra.vo.ResultWaringVO;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>批数据方案结果表-表信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultBaseRepositoryImpl extends BaseRepositoryImpl<BatchResultBase, BatchResultBaseDTO> implements BatchResultBaseRepository {

    private final BatchResultItemMapper batchResultItemMapper;

    public BatchResultBaseRepositoryImpl(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public Page<BatchResultBaseDTO> listResultBase(PageRequest pageRequest, BatchResultBaseDTO batchResultBaseDTO) {
        Page<BatchResultBaseDTO> page = self().pageAndSortDTO(pageRequest, batchResultBaseDTO);
        for (BatchResultBaseDTO dto : page.getContent()) {
            //获取base下所有校验项的告警等级Json
            List<String> warningLevelJsonList = batchResultItemMapper.selectWaringLevelJson(dto);
            //将所有告警等级Json转换合并成集合
            List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
            warningLevelJsonList.forEach(warningLevelJson -> warningLevelVOList.addAll(JsonUtils.json2WarningLevelVO(warningLevelJson)));
            //合并处理每个检验项的告警等级
            Map<String, Long> collect = warningLevelVOList.stream()
                    .collect(
                            Collectors.toMap(WarningLevelVO::getWarningLevel,
                                    WarningLevelVO::getLevelCount,
                                    Long::sum));
            List<ResultWaringVO> resultWaringVOList = new ArrayList<>();
            //返回base下的所有告警等级以及对应的数量
            collect.forEach((k, v) -> {
                ResultWaringVO resultWaringVO = ResultWaringVO.builder()
                        .warningLevel(k)
                        .countSum(v)
                        .build();
                resultWaringVOList.add(resultWaringVO);
            });
            dto.setResultWaringVOList(resultWaringVOList);
        }
        return page;
    }

    @Override
    public List<BatchResultBaseDTO> listResultBaseAll(BatchResultBaseDTO batchResultBaseDTO) {
        List<BatchResultBaseDTO> batchResultBaseDTOList = selectDTOByCondition(Condition.builder(BatchResultBase.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(BatchResultBase.FIELD_PROJECT_ID, batchResultBaseDTO.getProjectId())
                        .andEqualTo(BatchResultBase.FIELD_RESULT_ID, batchResultBaseDTO.getResultId())
                        .andEqualTo(BatchResultBase.FIELD_TENANT_ID, batchResultBaseDTO.getTenantId()))
                .build());
        for (BatchResultBaseDTO dto : batchResultBaseDTOList) {
            //获取base下所有校验项的告警等级Json
            List<String> warningLevelJsonList = batchResultItemMapper.selectWaringLevelJson(dto);
            //将所有告警等级Json转换合并成集合
            List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
            warningLevelJsonList.forEach(warningLevelJson -> warningLevelVOList.addAll(JsonUtils.json2WarningLevelVO(warningLevelJson)));
            //合并处理每个检验项的告警等级
            Map<String, Long> collect = warningLevelVOList.stream()
                    .collect(
                            Collectors.toMap(WarningLevelVO::getWarningLevel,
                                    WarningLevelVO::getLevelCount,
                                    Long::sum));
            List<ResultWaringVO> resultWaringVOList = new ArrayList<>();
            //返回base下的所有告警等级以及对应的数量
            collect.forEach((k, v) -> {
                ResultWaringVO resultWaringVO = ResultWaringVO.builder()
                        .warningLevel(k)
                        .countSum(v)
                        .build();
                resultWaringVOList.add(resultWaringVO);
            });
            dto.setResultWaringVOList(resultWaringVOList);
        }
        return batchResultBaseDTOList;
    }

    @Override
    public Long selectMaxResultBaseId(Long planBaseId, Long projectId) {
        return batchResultItemMapper.selectMaxResultBaseId(planBaseId,projectId);
    }
}
