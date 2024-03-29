package org.xdsp.quality.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.ResponseUtils;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.util.JsqlParser;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.app.service.BatchPlanBaseService;
import org.xdsp.quality.app.service.DataFieldService;
import org.xdsp.quality.domain.entity.BaseFormValue;
import org.xdsp.quality.domain.entity.BatchResult;
import org.xdsp.quality.domain.entity.PlanBaseAssign;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.repository.*;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.feign.ModelFeign;
import org.xdsp.quality.infra.mapper.DataFieldMapper;

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

    @Resource
    private DataFieldMapper dataFieldMapper;

    private final PlanBaseAssignRepository planBaseAssignRepository;
    private final BaseFormValueRepository baseFormValueRepository;
    private final DataFieldRepository dataFieldRepository;
    private final ModelFeign modelFeign;
    private final DataFieldService dataFieldService;
    private final PlanGroupRepository planGroupRepository;

    public BatchPlanBaseServiceImpl(PlanBaseAssignRepository planBaseAssignRepository, BaseFormValueRepository baseFormValueRepository, DataFieldRepository dataFieldRepository, ModelFeign modelFeign, DataFieldService dataFieldService, PlanGroupRepository planGroupRepository) {
        this.planBaseAssignRepository = planBaseAssignRepository;
        this.baseFormValueRepository = baseFormValueRepository;
        this.dataFieldRepository = dataFieldRepository;
        this.modelFeign = modelFeign;
        this.dataFieldService = dataFieldService;
        this.planGroupRepository = planGroupRepository;
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
        //获取方案的全路径path
        if (batchPlanBaseDTO.getParentGroupId() != null && batchPlanBaseDTO.getParentGroupId() != 0L) {
            String parentGroupPath = getParentGroupPath(batchPlanBaseDTO.getParentGroupId());
            batchPlanBaseDTO.setGroupPath(String.format("%s/%s",parentGroupPath,batchPlanBaseDTO.getGroupName()));
        }else{
            batchPlanBaseDTO.setGroupPath(batchPlanBaseDTO.getGroupName());
        }
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

    private String getParentGroupPath(Long groupId) {
        String groupPath = "";
        if (groupId == 0) {
            return StringUtils.EMPTY;
        }
        PlanGroup planGroup = planGroupRepository.selectByPrimaryKey(groupId);
        String parentGroupPath = getParentGroupPath(planGroup.getParentGroupId());
        if (StringUtils.isNotEmpty(parentGroupPath)) {
            groupPath = parentGroupPath + "/" + planGroup.getGroupName();
        } else {
            groupPath = planGroup.getGroupName();
        }
        return groupPath;
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
            List<BaseFormValueDTO> baseFormValueDTOList = batchPlanBaseDTO.getBaseFormValueDTOS();
            baseFormValueDTOList.forEach(baseFormValueDTO -> baseFormValueDTO.setPlanBaseId(batchPlanBaseDTO.getPlanBaseId()));
            baseFormValueRepository.batchInsertDTOSelective(baseFormValueDTOList);
        }
        // 如果类型为数据源且开启自动生成标准
        if ("TABLE".equals(batchPlanBaseDTO.getSqlType()) && batchPlanBaseDTO.getBuildRuleFlag() != null && batchPlanBaseDTO.getBuildRuleFlag() == 1) {
            // feign调用获取对应的表设计信息
            ResponseEntity<String> tableResponse = modelFeign.detailForFeign(
                    batchPlanBaseDTO.getTenantId(),
                    batchPlanBaseDTO.getProjectId(),
                    batchPlanBaseDTO.getDatasourceType(),
                    batchPlanBaseDTO.getDatasourceCode(),
                    batchPlanBaseDTO.getDatasourceSchema(),
                    batchPlanBaseDTO.getObjectName());
            // 获取表设计的表tableId
            CustomTableDTO dto = ResponseUtils.getResponse(tableResponse, CustomTableDTO.class,
                    (httpStatus, response) -> {
                        throw new CommonException(response);
                    }, exceptionResponse -> {
                        throw new CommonException(exceptionResponse.getMessage());
                    });
            if (dto != null) {
                List<TableColumnDTO> columnDTOList = dataFieldMapper.selectStandardColumn(dto.getCustomTableId());
                if (CollectionUtils.isNotEmpty(columnDTOList)) {
                    if (CollectionUtils.isNotEmpty(columnDTOList)) {
                        for (TableColumnDTO tableColumnDTO : columnDTOList) {
                            // 将字段标准转换为规则
                            BatchPlanFieldDTO batchPlanFieldDTO = dataFieldService.standardToRule(tableColumnDTO.getQuoteId(), tableColumnDTO.getColumnType());
                            insert(batchPlanFieldDTO, batchPlanBaseDTO);
                        }
                    }
                }
            }
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

    /**
     * 将字段标准生成的规则挂到表字段上
     */
    private void insert(BatchPlanFieldDTO batchPlanFieldDTO, BatchPlanBaseDTO batchPlanBaseDTO) {
        // 如果字段规则表下的字段，生成了标准则插入字段规则信息
        if (CollectionUtils.isNotEmpty(batchPlanFieldDTO.getBatchPlanFieldLineDTOList())) {
            batchPlanFieldDTO.setPlanBaseId(batchPlanBaseDTO.getPlanBaseId());
            batchPlanFieldDTO.setTenantId(batchPlanBaseDTO.getTenantId());
            batchPlanFieldDTO.setProjectId(batchPlanBaseDTO.getProjectId());
            batchPlanFieldRepository.insertDTOSelective(batchPlanFieldDTO);
            // 插入校验项
            for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldDTO.getBatchPlanFieldLineDTOList()) {
                batchPlanFieldLineDTO.setTenantId(batchPlanBaseDTO.getTenantId());
                batchPlanFieldLineDTO.setProjectId(batchPlanBaseDTO.getProjectId());
                batchPlanFieldLineDTO.setPlanRuleId(batchPlanFieldDTO.getPlanRuleId());
                batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
                // 遍历插入字段规则条件
                for (BatchPlanFieldConDTO batchPlanFieldConDTO : batchPlanFieldLineDTO.getBatchPlanFieldConDTOList()) {
                    batchPlanFieldConDTO.setTenantId(batchPlanBaseDTO.getTenantId());
                    batchPlanFieldConDTO.setProjectId(batchPlanBaseDTO.getProjectId());
                    batchPlanFieldConDTO.setPlanLineId(batchPlanFieldLineDTO.getPlanLineId());
                    batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
                }
            }
        }
    }
}
