package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/25 10:34
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE,sheetIndex = 0)
public class RuleGroupBatchImportServiceImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleGroupBatchImportServiceImpl(ObjectMapper objectMapper,
                                           RuleGroupRepository ruleGroupRepository) {
        this.objectMapper = objectMapper;
        this.ruleGroupRepository = ruleGroupRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (String json : data) {
                RuleGroupDTO ruleGroupDTO = objectMapper.readValue(json, RuleGroupDTO.class);
                ruleGroupDTO.setProjectId(projectId);
                ruleGroupDTO.setTenantId(tenantId);
                //根据分组Code在目标环境是否存在，若存在则更新
                List<RuleGroupDTO> ruleGroupDTOS = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class).andWhere(Sqls.custom()
                                .andEqualTo(RuleGroup.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(RuleGroup.FIELD_PROJECT_ID,projectId)
                                .andEqualTo(RuleGroup.FIELD_GROUP_CODE,ruleGroupDTO.getGroupCode()))
                        .build());
                if(CollectionUtils.isNotEmpty(ruleGroupDTOS)){
                    ruleGroupDTO.setGroupId(ruleGroupDTOS.get(0).getGroupId());
                    if(StringUtils.isNotEmpty(ruleGroupDTO.getParentGroupCode())){
                        List<RuleGroupDTO> parentRuleGroupDTOList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class).andWhere(Sqls.custom()
                                        .andEqualTo(RuleGroup.FIELD_TENANT_ID,tenantId)
                                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID,projectId)
                                        .andEqualTo(RuleGroup.FIELD_GROUP_CODE,ruleGroupDTO.getParentGroupCode()))
                                .build());
                        if(CollectionUtils.isNotEmpty(parentRuleGroupDTOList)){
                            ruleGroupDTO.setParentGroupId(parentRuleGroupDTOList.get(0).getGroupId());
                        }
                    }
                    //查询并设置父分组id
                    if(ruleGroupDTO.getParentGroupCode().equalsIgnoreCase("root")){
                        ruleGroupDTO.setParentGroupId(0L);
                    }

                    ruleGroupDTO.setObjectVersionNumber(ruleGroupDTOS.get(0).getObjectVersionNumber());
                    ruleGroupRepository.updateByDTOPrimaryKeySelective(ruleGroupDTO);
                }else {
                    //新增
                    //查询设置父分组id
                    if(StringUtils.isNotEmpty(ruleGroupDTO.getParentGroupCode())){
                        List<RuleGroupDTO> parentRuleGroupDTOList = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class).andWhere(Sqls.custom()
                                        .andEqualTo(RuleGroup.FIELD_TENANT_ID,tenantId)
                                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID,projectId)
                                        .andEqualTo(RuleGroup.FIELD_GROUP_CODE,ruleGroupDTO.getParentGroupCode()))
                                .build());
                        if(CollectionUtils.isNotEmpty(parentRuleGroupDTOList)){
                            ruleGroupDTO.setParentGroupId(parentRuleGroupDTOList.get(0).getGroupId());
                        }
                        //查询并设置父分组id
                        if(ruleGroupDTO.getParentGroupCode().equalsIgnoreCase("root") || StringUtils.isEmpty(ruleGroupDTO.getParentGroupCode())){
                            ruleGroupDTO.setParentGroupId(0L);
                        }
                    }
                    ruleGroupRepository.insertDTOSelective(ruleGroupDTO);
                }
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        return true;
    }
}
