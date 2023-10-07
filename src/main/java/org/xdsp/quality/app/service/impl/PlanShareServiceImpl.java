package org.xdsp.quality.app.service.impl;

import io.choerodon.core.convertor.ApplicationContextHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.quality.api.dto.PlanShareDTO;
import org.xdsp.quality.app.service.PlanShareService;
import org.xdsp.quality.domain.entity.BatchPlan;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.entity.PlanShare;
import org.xdsp.quality.domain.repository.BatchPlanRepository;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.domain.repository.PlanShareRepository;
import org.xdsp.quality.infra.mapper.PlanShareMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Service
public class PlanShareServiceImpl implements PlanShareService {

    private final PlanShareMapper planShareMapper;
    private final PlanShareRepository planShareRepository;
    private final BatchPlanRepository batchPlanRepository;

    public PlanShareServiceImpl(PlanShareMapper planShareMapper, PlanShareRepository planShareRepository, BatchPlanRepository batchPlanRepository) {
        this.planShareMapper = planShareMapper;
        this.planShareRepository = planShareRepository;
        this.batchPlanRepository = batchPlanRepository;
    }

    @Override
    public List<PlanShareDTO> shareProjects(Long planId) {
        //查询方案共享的项目
        return  planShareMapper.shareProjects(planId);
    }

    /**
     * 共享方案分组
     *
     * @param batchPlan
     * @param projectId
     */
    private void sharePlanGroup(BatchPlan batchPlan, Long projectId) {
        //获取方案的分组，一直找到根路径
        List<PlanGroup> allGroups = getParentGroup(batchPlan.getGroupId());
        if (CollectionUtils.isNotEmpty(allGroups)) {
            //分享这个方案所在的分组
            allGroups.forEach(planGroup -> {
                int exist = planShareRepository.selectCount(PlanShare.builder()
                        .shareObjectType("GROUP")
                        .shareObjectId(planGroup.getGroupId())
                        .shareToProjectId(projectId)
                        .build());
                //分组没有共享给这个项目，则进行共享
                if (exist == 0) {
                    PlanShare groupShare = PlanShare.builder()
                            .shareObjectType("GROUP")
                            .shareObjectId(planGroup.getGroupId())
                            .shareFromProjectId(planGroup.getProjectId())
                            .shareToProjectId(projectId)
                            .tenantId(planGroup.getTenantId())
                            .projectId(planGroup.getProjectId())
                            .build();
                    planShareRepository.insert(groupShare);
                }
            });
        }
    }

    /**
     * 获取所有父分组
     *
     * @param groupId
     * @return
     */
    public List<PlanGroup> getParentGroup(Long groupId) {
        PlanGroupRepository planGroupRepository = ApplicationContextHelper.getContext().getBean(PlanGroupRepository.class);
        PlanGroup planGroup = planGroupRepository.selectByPrimaryKey(groupId);
        List<PlanGroup> allGroupList = new ArrayList<>();
        allGroupList.add(planGroup);
        if (planGroup.getParentGroupId() != null && planGroup.getParentGroupId() != 0) {
            List<PlanGroup> parentGroups = getParentGroup(planGroup.getParentGroupId());
            allGroupList.addAll(parentGroups);
        }
        return allGroupList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PlanShareDTO> batchShare(Long tenantId, Long projectId, Long planId, List<PlanShareDTO> planShareDTOList) {
        //方案共享
        //删除方案的原始共享
        List<PlanShare> planShares = planShareRepository.select(PlanShare.builder().shareObjectType("PLAN").shareObjectId(planId).build());
        planShareRepository.batchDeleteByPrimaryKey(planShares);
        if(CollectionUtils.isEmpty(planShareDTOList)){
            return planShareDTOList;
        }

        BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
        //进行方案共享
        planShareDTOList.forEach(planShareDTO -> {
            //共享分组
            sharePlanGroup(batchPlan,planShareDTO.getShareToProjectId());
            //共享方案
            sharePlan(batchPlan,planShareDTO.getShareToProjectId());
        });
        return planShareDTOList;
    }

    private void sharePlan(BatchPlan batchPlan, Long projectId) {
        //判断是否进行了项目共享，没有则将方案共享到此执行项目下
        int exist = planShareRepository.selectCount(PlanShare.builder()
                .shareObjectType("PLAN")
                .shareObjectId(batchPlan.getPlanId())
                .shareToProjectId(projectId)
                .build());
        if (exist == 0) {
            //如果没有共享，则将此方案共享到项目下去
            planShareRepository.insert(PlanShare.builder()
                    .shareObjectType("PLAN")
                    .shareObjectId(batchPlan.getPlanId())
                    .shareFromProjectId(batchPlan.getProjectId())
                    .shareToProjectId(projectId)
                    .tenantId(batchPlan.getTenantId())
                    .projectId(batchPlan.getProjectId())
                    .build());
        }
    }
}
