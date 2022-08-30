package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.app.service.PlanGroupService;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.mapper.BatchPlanBaseMapper;
import com.hand.hdsp.quality.infra.mapper.BatchPlanFieldMapper;
import com.hand.hdsp.quality.infra.mapper.BatchPlanRelTableMapper;
import com.hand.hdsp.quality.infra.mapper.BatchPlanTableMapper;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static com.hand.hdsp.quality.infra.constant.GroupType.BATCH;

/**
 * <p>评估方案分组表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class PlanGroupServiceImpl implements PlanGroupService {

    private final PlanGroupRepository planGroupRepository;
    private final BatchPlanRepository batchPlanRepository;
    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableMapper batchPlanTableMapper;
    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldMapper batchPlanFieldMapper;
    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchPlanRelTableMapper batchPlanRelTableMapper;
    private final BatchPlanBaseMapper batchPlanBaseMapper;

    public PlanGroupServiceImpl(PlanGroupRepository planGroupRepository,
                                BatchPlanRepository batchPlanRepository,
                                BatchPlanBaseRepository batchPlanBaseRepository,
                                BatchPlanTableRepository batchPlanTableRepository,
                                BatchPlanTableMapper batchPlanTableMapper, BatchPlanFieldRepository batchPlanFieldRepository,
                                BatchPlanFieldMapper batchPlanFieldMapper, BatchPlanRelTableRepository batchPlanRelTableRepository,
                                BatchPlanRelTableMapper batchPlanRelTableMapper, BatchPlanBaseMapper batchPlanBaseMapper) {
        this.planGroupRepository = planGroupRepository;
        this.batchPlanRepository = batchPlanRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableMapper = batchPlanTableMapper;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldMapper = batchPlanFieldMapper;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableMapper = batchPlanRelTableMapper;
        this.batchPlanBaseMapper = batchPlanBaseMapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(PlanGroupDTO planGroupDTO) {
        List<PlanGroupDTO> planGroupDTOList = planGroupRepository.selectDTO(PlanGroup.FIELD_PARENT_GROUP_ID, planGroupDTO.getGroupId());
        List<BatchPlanDTO> batchPlanDTOList = batchPlanRepository.selectDTO(BatchPlan.FIELD_GROUP_ID, planGroupDTO.getGroupId());
        //当前分组下存在评估方案，请删除评估方案后再执行删除操作！
        if (!planGroupDTOList.isEmpty() || !batchPlanDTOList.isEmpty()) {
            throw new CommonException(ErrorCode.EXISTS_OTHER_PLAN);
        }
        return planGroupRepository.deleteByPrimaryKey(planGroupDTO);
    }

    @Override
    public List<PlanGroupDTO> export(PlanGroupDTO dto, ExportParam exportParam) {
        List<PlanGroupDTO> planGroupDTOList = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(PlanGroup.FIELD_GROUP_TYPE, BATCH)
                        .andEqualTo(PlanGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(PlanGroup.FIELD_PROJECT_ID, ProjectHelper.getProjectId())
                        .andEqualTo(PlanGroup.FIELD_GROUP_ID,dto.getGroupId(),true))
                .build());

        planGroupDTOList.forEach(planGroupDTO -> {
            //查询分组下的评估方案
            List<BatchPlanDTO> batchPlanDTOList = batchPlanRepository.selectDTOByCondition(Condition.builder(BatchPlan.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(BatchPlan.FIELD_PROJECT_ID, planGroupDTO.getProjectId())
                            .andEqualTo(BatchPlan.FIELD_GROUP_ID, planGroupDTO.getGroupId())
                            .andEqualTo(BatchPlan.FIELD_TENANT_ID, planGroupDTO.getTenantId())
                            .andEqualTo(BatchPlan.FIELD_PLAN_NAME,dto.getPlanName(),true)
                    )
                    .build());
            batchPlanDTOList.stream().parallel().forEach(batchPlanDTO -> {
                //查询评估方案下的基础配置
                List<BatchPlanBaseDTO> batchPlanBaseDTOList = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PROJECT_ID, batchPlanDTO.getProjectId())
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_ID, batchPlanDTO.getPlanId())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanDTO.getTenantId())
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE,dto.getPlanBaseCode(),true)
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_NAME,dto.getPlanBaseName(),true)
                                .andEqualTo(BatchPlanBase.FIELD_OBJECT_NAME,dto.getObjectName(),true)
                                .andEqualTo(BatchPlanBase.FIELD_DESCRIPTION,dto.getDescription(),true)
                                .andEqualTo(BatchPlanBase.FIELD_DATASOURCE_SCHEMA,dto.getDatasourceSchema(),true)
                        )
                        .build());

                //fieldNum tableNum relTableNum currentPlanName
                batchPlanBaseDTOList.forEach(batchPlanBaseDTO -> {
                    //查询基础配置下的表级，字段，表间规则
                    getPlanTable(batchPlanBaseDTO);
                    getPlanField(batchPlanBaseDTO);
                    getPlanRelTable(batchPlanBaseDTO);
                });
                batchPlanDTO.setBatchPlanBaseDTOList(batchPlanBaseDTOList);
            });
            planGroupDTO.setBatchPlanDTOList(batchPlanDTOList);
        });
        return planGroupDTOList;
    }

    private void getPlanTable(BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setBatchPlanTableDTOList(batchPlanTableMapper.getPlanTable(batchPlanBaseDTO.getPlanBaseId()));
    }

    private void getPlanField(BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setBatchPlanFieldDTOList(batchPlanFieldMapper.getPlanField(batchPlanBaseDTO.getPlanBaseId()));
    }

    private void getPlanRelTable(BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setBatchPlanRelTableDTOList(batchPlanRelTableMapper.getRelTable(batchPlanBaseDTO.getPlanBaseId()));
    }
}
