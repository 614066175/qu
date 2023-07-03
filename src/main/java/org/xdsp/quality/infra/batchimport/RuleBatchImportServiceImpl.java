package org.xdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.CommonGroupClient;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.api.dto.RuleLineDTO;
import org.xdsp.quality.domain.entity.Rule;
import org.xdsp.quality.domain.repository.RuleGroupRepository;
import org.xdsp.quality.domain.repository.RuleLineRepository;
import org.xdsp.quality.domain.repository.RuleRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.io.IOException;
import java.util.List;

import static org.xdsp.core.infra.constant.CommonGroupConstants.GroupType.STANDARD_RULE;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/09 16:41
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE, sheetIndex = 1)
public class RuleBatchImportServiceImpl extends BatchImportHandler implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RuleRepository ruleRepository;
    private final RuleGroupRepository ruleGroupRepository;
    private final RuleLineRepository ruleLineRepository;
    private final CommonGroupRepository commonGroupRepository;

    @Autowired
    private CommonGroupClient commonGroupClient;

    public RuleBatchImportServiceImpl(ObjectMapper objectMapper,
                                      RuleRepository ruleRepository,
                                      RuleGroupRepository ruleGroupRepository,
                                      RuleLineRepository ruleLineRepository, CommonGroupRepository commonGroupRepository) {
        this.objectMapper = objectMapper;
        this.ruleRepository = ruleRepository;
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleLineRepository = ruleLineRepository;
        this.commonGroupRepository = commonGroupRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (int i = 0; i < data.size(); i++) {
                String json = data.get(i);
                RuleDTO ruleDTO = objectMapper.readValue(json, RuleDTO.class);
                if ("Y".equals(ruleDTO.getIsPlatformFlag())) {
                    addBackInfo(i, "平台级数据不进行导入");
                    continue;
                }
                //导入分组id
                CommonGroup commonGroup = commonGroupRepository.selectOne(CommonGroup.builder()
                        .groupType(STANDARD_RULE)
                        .groupPath(ruleDTO.getGroupPath())
                        .tenantId(tenantId).projectId(projectId)
                        .build());
                if (commonGroup == null) {
                    //不存在直接新建
                    commonGroupClient.createGroup(tenantId, projectId, STANDARD_RULE, ruleDTO.getGroupPath());
                    CommonGroup group = commonGroupRepository.selectOne(CommonGroup.builder()
                            .groupType(STANDARD_RULE)
                            .groupPath(ruleDTO.getGroupPath())
                            .tenantId(tenantId).projectId(projectId).build());
                    ruleDTO.setGroupId(group.getGroupId());
                } else {
                    ruleDTO.setGroupId(commonGroup.getGroupId());
                }
                ruleDTO.setTenantId(tenantId);
                ruleDTO.setProjectId(projectId);
                if ("是".equals(ruleDTO.getExceptionBlockFlag())) {
                    ruleDTO.setExceptionBlock(1);
                } else {
                    ruleDTO.setExceptionBlock(0);
                }
                //判断标准规则是否存在
                Rule exist = ruleRepository.selectOne(Rule.builder().ruleCode(ruleDTO.getRuleCode())
                        .tenantId(tenantId)
                        .projectId(projectId).build());
                if (exist != null) {
                    ruleDTO.setRuleId(exist.getRuleId());
                    ruleDTO.setEnabledFlag(exist.getEnabledFlag());
                    ruleDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    ruleRepository.updateByDTOPrimaryKey(ruleDTO);
                } else {
                    ruleRepository.insertDTOSelective(ruleDTO);
                }
                //删除之前的检验项
                if (exist != null) {
                    ruleLineRepository.deleteByParentId(exist.getRuleId());
                }
                //导入校验项
                ruleLineRepository.insertDTOSelective(RuleLineDTO.builder()
                        .ruleId(ruleDTO.getRuleId())
                        .checkWay(ruleDTO.getCheckWay())
                        .checkItem(ruleDTO.getCheckItem())
                        .countType(ruleDTO.getCountType())
                        .compareWay(ruleDTO.getCompareWay())
                        .regularExpression(ruleDTO.getRegularExpression())
                        .warningLevel(ruleDTO.getWarningLevel())
                        .tenantId(tenantId)
                        .projectId(projectId)
                        .build());
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
