package org.xdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.xdsp.quality.infra.constant.GroupType.BATCH;

/**
 * <p>
 * 使用sheetName和sheetIndex都行，sheetIndex从0开始(默认值)
 * </p>
 *
 * @author lgl 2021/03/11 14:50
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 0)
public class PlanGroupBatchImportImpl extends BatchImportHandler implements IBatchImportService {
    private final ObjectMapper objectMapper;

    private final PlanGroupRepository planGroupRepository;

    private final Long ALLGROUP = 0L;

    public PlanGroupBatchImportImpl(ObjectMapper objectMapper, PlanGroupRepository planGroupRepository) {
        this.objectMapper = objectMapper;
        this.planGroupRepository = planGroupRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<PlanGroupDTO> planGroups = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                PlanGroupDTO planGroup = objectMapper.readValue(json, PlanGroupDTO.class);
                planGroup.setTenantId(tenantId);
                planGroup.setGroupType(BATCH);
                planGroup.setProjectId(projectId);
                planGroups.add(planGroup);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }

        int i = 0;
        for (PlanGroupDTO planGroup : planGroups) {
            //如果不存在则新建
            PlanGroup group = planGroupRepository.selectOne(PlanGroup.builder().groupCode(planGroup.getGroupCode())
                    .tenantId(planGroup.getTenantId())
                    .projectId(planGroup.getProjectId())
                    .build());

            if (StringUtils.isNotEmpty(planGroup.getParentGroupCode())) {
                //如果存在父编码
                PlanGroup parentGroup = planGroupRepository.selectOne(PlanGroup.builder().groupCode(planGroup.getParentGroupCode())
                        .tenantId(planGroup.getTenantId())
                        .projectId(planGroup.getProjectId())
                        .build());
                if (parentGroup == null) {
                    addErrorMsg(i, planGroup.getParentGroupCode() + "父分组不存在");
                    return false;
                }
                planGroup.setParentGroupId(parentGroup.getGroupId());
            }else{
                planGroup.setParentGroupId(ALLGROUP);
            }
            if (group != null) {
                //修改分组名称分组描述父分组编码
                group.setGroupName(planGroup.getGroupName());
                group.setGroupDesc(planGroup.getGroupDesc());
                group.setParentGroupId(planGroup.getParentGroupId());
                planGroupRepository.updateAllColumnWhereTenant(group, group.getTenantId());
            } else {
                planGroupRepository.insertDTOSelective(planGroup);
            }
            i++;
        }

        return true;
    }
}
