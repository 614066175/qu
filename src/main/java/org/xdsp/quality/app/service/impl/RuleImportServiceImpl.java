package org.xdsp.quality.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.api.dto.RuleGroupDTO;
import org.xdsp.quality.domain.entity.RuleGroup;
import org.xdsp.quality.domain.repository.RuleGroupRepository;
import org.xdsp.quality.domain.repository.RuleRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/09 16:34
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE)
public class RuleImportServiceImpl implements IDoImportService {
    private final ObjectMapper objectMapper;
    private final RuleRepository ruleRepository;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleImportServiceImpl(ObjectMapper objectMapper, RuleRepository ruleRepository, RuleGroupRepository ruleGroupRepository) {
        this.objectMapper = objectMapper;
        this.ruleRepository = ruleRepository;
        this.ruleGroupRepository = ruleGroupRepository;
    }


    @Override
    public Boolean doImport(String data) {
        RuleDTO ruleDTO;
        try {
            ruleDTO = objectMapper.readValue(data, RuleDTO.class);
        } catch (IOException e) {
            // 失败
            return false;
        }
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        ruleDTO.setTenantId(tenantId);
        List<RuleGroupDTO> ruleGroupDTOS = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_GROUP_CODE, ruleDTO.getGroupCode())
                        .andEqualTo(RuleGroup.FIELD_TENANT_ID, ruleDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(ruleGroupDTOS)) {
            ruleDTO.setGroupId(ruleGroupDTOS.get(0).getGroupId());
        } else {
            //创建分组
            RuleGroupDTO ruleGroupDTO = RuleGroupDTO.builder()
                    .groupCode(ruleDTO.getGroupCode())
                    .groupDesc(ruleDTO.getGroupDesc())
                    .groupName(ruleDTO.getGroupName())
                    .tenantId(ruleDTO.getTenantId())
                    .build();
            ruleGroupRepository.insertDTOSelective(ruleGroupDTO);
            ruleDTO.setGroupId(ruleGroupDTO.getGroupId());
        }
        // 插入数据
        ruleRepository.insertDTOSelective(ruleDTO);
        return true;
    }
}
