package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BaseFormValueDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanBaseMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.hzero.starter.driver.core.infra.util.PageParseUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>批数据方案-基础配置表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanBaseRepositoryImpl extends BaseRepositoryImpl<BatchPlanBase, BatchPlanBaseDTO> implements BatchPlanBaseRepository {

    private final BatchPlanBaseMapper batchPlanBaseMapper;

    public BatchPlanBaseRepositoryImpl(BatchPlanBaseMapper batchPlanBaseMapper) {
        this.batchPlanBaseMapper = batchPlanBaseMapper;

    }

    @Override
    public Page<BatchPlanBaseDTO> list(PageRequest pageRequest, BatchPlanBaseDTO batchPlanBaseDTO) {
        //如果没有点击具体的评估方案，则查询分组下所有评估方案的质检项
        if (batchPlanBaseDTO.getPlanId() == null && batchPlanBaseDTO.getGroupId() != null) {
            //递归查询此分组下所有子分组
            List<PlanGroup> subGroupList = getSubGroup(batchPlanBaseDTO.getGroupId());
            PlanGroupRepository planGroupRepository = ApplicationContextHelper.getContext().getBean(PlanGroupRepository.class);
            //并加上自己
            PlanGroup planGroup = planGroupRepository.selectByPrimaryKey(batchPlanBaseDTO.getGroupId());
            //所有分组（groupId=0）无需考虑
            if (planGroup != null) {
                subGroupList.add(planGroup);
            }
            //查询所有分组下的评估方案
            List<Long> groupIds = subGroupList.stream().map(PlanGroup::getGroupId).collect(Collectors.toList());
            BatchPlanRepository batchPlanRepository = ApplicationContextHelper.getContext().getBean(BatchPlanRepository.class);
            List<BatchPlanDTO> batchPlanDTOS = batchPlanRepository.selectDTOByCondition(Condition.builder(BatchPlan.class)
                    .andWhere(Sqls.custom()
                            .andIn(BatchPlan.FIELD_GROUP_ID, groupIds)
                            .andEqualTo(BatchPlan.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId())
                            .andEqualTo(BatchPlan.FIELD_PROJECT_ID, batchPlanBaseDTO.getProjectId()))
                    .build());
            if (CollectionUtils.isEmpty(batchPlanDTOS)) {
                return new Page<>();
            }
            List<Long> planIds = batchPlanDTOS.stream().map(BatchPlanDTO::getPlanId).collect(Collectors.toList());
            batchPlanBaseDTO.setPlanIds(planIds);
            //如果没有动态表单的查询条件
            if (CollectionUtils.isEmpty(batchPlanBaseDTO.getBaseFormValueDTOS())) {
                return planBaseSelect(batchPlanBaseDTO, pageRequest);
            }
            //查询各自条件的结果集取交集
            return formConditionSelect(batchPlanBaseDTO, pageRequest);
        }
        if (CollectionUtils.isEmpty(batchPlanBaseDTO.getBaseFormValueDTOS())) {
            return planBaseSelect(batchPlanBaseDTO, pageRequest);
        }
        return formConditionSelect(batchPlanBaseDTO, pageRequest);
    }

    private Page<BatchPlanBaseDTO> planBaseSelect(BatchPlanBaseDTO batchPlanBaseDTO, PageRequest pageRequest) {
        //查询所有评估方案的质检项,因为涉及到头行结构，不能直接分页查询，会导致合并后数据量不对，采取手工分页
        //todo 如果数据量大的情况下，怎么考虑
        List<BatchPlanBaseDTO> list = batchPlanBaseMapper.list(batchPlanBaseDTO);
        Page<BatchPlanBaseDTO> page = PageParseUtil.springPage2C7nPage(PageUtil.doPage(list, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize())));
        //转换质检项表单数据
        page.getContent().forEach(dto -> {
            if (CollectionUtils.isNotEmpty(dto.getBaseFormValueDTOS())) {
                HashMap<String, Object> map = new HashMap<>();
                dto.getBaseFormValueDTOS()
                        .stream()
                        .filter(baseFormValueDTO -> StringUtils.isNotEmpty(baseFormValueDTO.getItemCode()))
                        .forEach(baseFormValueDTO -> map.put(baseFormValueDTO.getItemCode(), baseFormValueDTO.getFormValue()));
                dto.setBaseFormValueJson(JsonUtil.toJson(map));
            }
        });
        return page;
    }

    /**
     * 根据动态表单传参情况进行查询
     *
     * @param batchPlanBaseDTO
     * @return
     */
    private Page<BatchPlanBaseDTO> formConditionSelect(BatchPlanBaseDTO batchPlanBaseDTO, PageRequest pageRequest) {
        List<BaseFormValueDTO> baseFormValueDTOS = batchPlanBaseDTO.getBaseFormValueDTOS();
        List<BatchPlanBaseDTO> resultBatchBaseDTOList = new ArrayList<>();
        for (BaseFormValueDTO baseFormValueDTO : baseFormValueDTOS) {
            batchPlanBaseDTO.setBaseFormValueDTOS(Collections.singletonList(baseFormValueDTO));
            List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseMapper.list(batchPlanBaseDTO);
            //只要存在一个条件没有结果直接返回
            if (CollectionUtils.isEmpty(batchPlanBaseDTOS)) {
                return new Page<>();
            }
            //第一次结果直接赋值
            if (CollectionUtils.isEmpty(resultBatchBaseDTOList)) {
                resultBatchBaseDTOList = batchPlanBaseDTOS;
                continue;
            }
            List<Long> resultBaseIds = batchPlanBaseDTOS.stream()
                    .map(BatchPlanBaseDTO::getPlanBaseId)
                    .collect(Collectors.toList());
            //取交集
            resultBatchBaseDTOList = resultBatchBaseDTOList.stream()
                    .filter(dto -> resultBaseIds.contains(dto.getPlanBaseId()))
                    .collect(Collectors.toList());
            //只要取交集没有结果直接返回
            if (CollectionUtils.isEmpty(resultBatchBaseDTOList)) {
                return new Page<>();
            }
        }
        //拿到结果集后，然后在执行一次查询。获取全部表单数据
        List<Long> baseIds = resultBatchBaseDTOList.stream().map(BatchPlanBaseDTO::getPlanBaseId).collect(Collectors.toList());
        List<BatchPlanBaseDTO> list = batchPlanBaseMapper.list(BatchPlanBaseDTO.builder().planBaseIds(baseIds).tenantId(batchPlanBaseDTO.getTenantId()).build());
        Page<BatchPlanBaseDTO> page = PageParseUtil.springPage2C7nPage(PageUtil.doPage(list, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize())));
        //转换质检项表单数据
        page.getContent().forEach(dto -> {
            if (CollectionUtils.isNotEmpty(dto.getBaseFormValueDTOS())) {
                HashMap<String, Object> map = new HashMap<>();
                dto.getBaseFormValueDTOS()
                        .stream()
                        .filter(baseFormValueDTO -> StringUtils.isNotEmpty(baseFormValueDTO.getItemCode()))
                        .forEach(baseFormValueDTO ->
                                map.put(baseFormValueDTO.getItemCode(), baseFormValueDTO.getFormValue()));
                dto.setBaseFormValueJson(JsonUtil.toJson(map));
            }
        });
        return page;
    }

    /**
     * 获取子分组
     *
     * @param groupId
     * @return
     */
    private List<PlanGroup> getSubGroup(Long groupId) {
        PlanGroupRepository planGroupRepository = ApplicationContextHelper.getContext().getBean(PlanGroupRepository.class);
        List<PlanGroup> subGroupList = planGroupRepository.select(PlanGroup.builder()
                .parentGroupId(groupId).build());
        List<PlanGroup> allSubGroupList = new ArrayList<>(subGroupList);
        if (CollectionUtils.isNotEmpty(subGroupList)) {
            subGroupList.forEach(subGroup -> {
                List<PlanGroup> subPlanGroups = getSubGroup(subGroup.getGroupId());
                allSubGroupList.addAll(subPlanGroups);
            });
        }
        return allSubGroupList;
    }

    @Override
    public BatchPlanBaseDTO detail(Long planBaseId) {
        return batchPlanBaseMapper.detail(planBaseId);
    }
}
