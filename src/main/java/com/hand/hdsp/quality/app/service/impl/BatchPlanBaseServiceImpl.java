package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.core.util.JsqlParser;
import com.hand.hdsp.quality.api.dto.BaseFormValueDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.ColumnDTO;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.BaseFormValue;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.entity.PlanBaseAssign;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
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

    private final PlanBaseAssignRepository planBaseAssignRepository;
    private final BaseFormValueRepository baseFormValueRepository;

    public BatchPlanBaseServiceImpl(PlanBaseAssignRepository planBaseAssignRepository, BaseFormValueRepository baseFormValueRepository) {
        this.planBaseAssignRepository = planBaseAssignRepository;
        this.baseFormValueRepository = baseFormValueRepository;
    }

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

        //删除所有此质检项的分配关系
        List<PlanBaseAssign> planBaseAssignList = planBaseAssignRepository.select(PlanBaseAssign.builder().planBaseId(planBaseId).build());
        planBaseAssignRepository.batchDeleteByPrimaryKey(planBaseAssignList);

        //删除此质检相关的所有动态表单的值
        List<BaseFormValue> baseFormValueList = baseFormValueRepository.select(BaseFormValue.builder().planBaseId(planBaseId).build());
        baseFormValueRepository.batchDeleteByPrimaryKey(baseFormValueList);

        return batchPlanBaseRepository.deleteByPrimaryKey(batchPlanBaseDTO);
    }

    @Override
    public BatchPlanBaseDTO detail(Long planBaseId, Long currentPlanId, Long tenantId) {
        BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseRepository.detail(planBaseId);
        batchPlanBaseDTO.setDatasourceName(batchPlanBaseDTO.getDatasourceCode());
        //如果传了当前方案id,则判断编辑标识，当前方案和所属方案不一致，则返回不可编辑标识
        if (currentPlanId != null) {
            if (!currentPlanId.equals(batchPlanBaseDTO.getPlanId())) {
                batchPlanBaseDTO.setEditFlag(0);
            } else {
                batchPlanBaseDTO.setEditFlag(1);
            }
        }
        if (CollectionUtils.isNotEmpty(batchPlanBaseDTO.getBaseFormValueDTOS())) {
            HashMap<String, Object> map = new HashMap<>();
            batchPlanBaseDTO.getBaseFormValueDTOS().stream()
                    .filter(baseFormValueDTO -> StringUtils.isNotEmpty(baseFormValueDTO.getItemCode()))
                    .forEach(baseFormValueDTO -> map.put(baseFormValueDTO.getItemCode(), baseFormValueDTO.getFormValue()));
            batchPlanBaseDTO.setBaseFormValueJson(JsonUtil.toJson(map));
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAssign(BatchPlanBaseDTO batchPlanBaseDTO) {
        //当前评估方案移除此质检项
        List<PlanBaseAssign> planBaseAssignList = planBaseAssignRepository.select(PlanBaseAssign.builder()
                .planBaseId(batchPlanBaseDTO.getPlanBaseId())
                .planId(batchPlanBaseDTO.getCurrentPlanId())
                .tenantId(batchPlanBaseDTO.getTenantId())
                .projectId(batchPlanBaseDTO.getProjectId())
                .build());
        planBaseAssignRepository.batchDeleteByPrimaryKey(planBaseAssignList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchPlanBaseDTO create(BatchPlanBaseDTO batchPlanBaseDTO) {
        //插入质检项
        batchPlanBaseRepository.insertDTOSelective(batchPlanBaseDTO);
        //如果有动态表单的值
        if (CollectionUtils.isNotEmpty(batchPlanBaseDTO.getBaseFormValueDTOS())) {
            List<BaseFormValueDTO> baseFormValueDTOS = batchPlanBaseDTO.getBaseFormValueDTOS();
            baseFormValueDTOS.forEach(baseFormValueDTO -> baseFormValueDTO.setPlanBaseId(batchPlanBaseDTO.getPlanBaseId()));
            baseFormValueRepository.batchInsertDTOSelective(baseFormValueDTOS);
        }
        return batchPlanBaseDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchPlanBaseDTO update(BatchPlanBaseDTO batchPlanBaseDTO) {
        //更新质检项
        batchPlanBaseRepository.updateDTOAllColumnWhereTenant(batchPlanBaseDTO, batchPlanBaseDTO.getTenantId());
        //删除此质检项的旧的动态表单数据
        List<BaseFormValue> baseFormValues = baseFormValueRepository.select(BaseFormValue.builder().planBaseId(batchPlanBaseDTO.getPlanBaseId()).build());
        baseFormValueRepository.batchDeleteByPrimaryKey(baseFormValues);
        //如果有动态表单的值
        if (CollectionUtils.isNotEmpty(batchPlanBaseDTO.getBaseFormValueDTOS())) {
            List<BaseFormValueDTO> baseFormValueDTOS = batchPlanBaseDTO.getBaseFormValueDTOS();
            baseFormValueDTOS.forEach(baseFormValueDTO -> baseFormValueDTO.setPlanBaseId(batchPlanBaseDTO.getPlanBaseId()));
            baseFormValueRepository.batchInsertDTOSelective(baseFormValueDTOS);
        }
        return batchPlanBaseDTO;
    }
}
