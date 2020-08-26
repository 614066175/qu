package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.core.util.JsqlParser;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.ColumnDTO;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>批数据方案-基础配置表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanBaseServiceImpl implements BatchPlanBaseService {

    @Resource
    private BatchPlanBaseRepository batchPlanBaseRepository;
    @Resource
    private BatchPlanTableRepository batchPlanTableRepository;
    @Resource
    private BatchPlanTableLineRepository batchPlanTableLineRepository;
    @Resource
    private BatchPlanTableConRepository batchPlanTableConRepository;
    @Resource
    private BatchPlanFieldRepository batchPlanFieldRepository;
    @Resource
    private BatchPlanFieldLineRepository batchPlanFieldLineRepository;
    @Resource
    private BatchPlanFieldConRepository batchPlanFieldConRepository;
    @Resource
    private BatchPlanRelTableRepository batchPlanRelTableRepository;
    @Resource
    private BatchResultRepository batchResultRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanBaseDTO batchPlanBaseDTO) {
        Long planBaseId = batchPlanBaseDTO.getPlanBaseId();
        //判断能否删除
        List<BatchResultDTO> batchResultDTOList = batchResultRepository.selectDTO(
                BatchResult.FIELD_PLAN_ID, batchPlanBaseDTO.getPlanId());
        if (CollectionUtils.isNotEmpty(batchResultDTOList)
                && PlanConstant.PlanStatus.RUNNING.equals(batchResultDTOList.get(0).getPlanStatus())) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }

        //删除表级规则条件
        batchPlanTableConRepository.deleteByPlanBaseId(planBaseId);
        //删除表级规则行
        batchPlanTableLineRepository.deleteByPlanBaseId(planBaseId);
        //删除表级规则
        batchPlanTableRepository.deleteByPlanBaseId(planBaseId);

        //删除字段规则条件
        batchPlanFieldConRepository.deleteByPlanBaseId(planBaseId);
        //删除字段规则行
        batchPlanFieldLineRepository.deleteByPlanBaseId(planBaseId);
        //删除字段规则
        batchPlanFieldRepository.deleteByPlanBaseId(planBaseId);

        //删除表间规则
        batchPlanRelTableRepository.deleteByPlanBaseId(planBaseId);

        return batchPlanBaseRepository.deleteByPrimaryKey(batchPlanBaseDTO);
    }

    @Override
    public BatchPlanBaseDTO detail(Long planBaseId, Long tenantId) {
        BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseRepository.detail(planBaseId);
        batchPlanBaseDTO.setDatasourceName(batchPlanBaseDTO.getDatasourceCode());
        return batchPlanBaseDTO;
    }

    @Override
    public List<ColumnDTO> columns(String sql) {
        List<Map<String, Object>> fields = JsqlParser.getFields(sql);
        return fields.stream().map(map -> ColumnDTO.builder()
                .colName((String) map.get("fieldName"))
                .typeName("VARCHAR")
                .build()).collect(Collectors.toList());
    }
}
