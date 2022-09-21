package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DOC;

/**
 * @author StoneHell
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC,sheetIndex = 1)})
public class StandardDocValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;
    private final StandardDocMapper standardDocMapper;
    private final StandardGroupRepository standardGroupRepository;

    public StandardDocValidator(ObjectMapper objectMapper,
                                StandardDocRepository standardDocRepository,
                                StandardDocMapper standardDocMapper,
                                StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.standardDocRepository = standardDocRepository;
        this.standardDocMapper = standardDocMapper;
        this.standardGroupRepository = standardGroupRepository;
    }


    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (int i = 0; i < data.size(); i++) {
                if (StringUtils.isNoneBlank(data.get(i))) {
                    StandardDocDTO standardDocDTO = objectMapper.readValue(data.get(i), StandardDocDTO.class);
                    List<StandardDocDTO> standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(StandardDoc.FIELD_STANDARD_CODE, standardDocDTO.getStandardCode())
                                    .andEqualTo(StandardDoc.FIELD_TENANT_ID, tenantId))
                            .build());
                    //标准编码存在
                    if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
                        addErrorMsg(i, "标准编码存已在");
                        return false;
                    }
                    standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName())
                                    .andEqualTo(StandardDoc.FIELD_TENANT_ID, tenantId))
                            .build());
                    //标准名称存在
                    if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
                        addErrorMsg(i, "标准名称已存在");
                        return false;
                    }

                    //标准编码不符合规范
                    if (!standardDocDTO.getStandardCode().matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                        addErrorMsg(i, "标准编码不符合:1-64个字符，可包含字母、数字或下划线”_”，英文字母开头");
                        return false;
                    }

                    // 对责任人进行验证
                    List<Long> chargeId = standardDocMapper.selectIdByChargeName(standardDocDTO.getChargeName(), tenantId);
                    if (CollectionUtils.isEmpty(chargeId)) {
                        addErrorMsg(i, "未找到此责任人，请检查数据");
                        return false;
                    }
                    //如果有责任部门，则进行验证
                    if (Strings.isNotEmpty(standardDocDTO.getChargeDeptName())) {
                        String chargeDeptName = standardDocDTO.getChargeDeptName();
                        if (DataSecurityHelper.isTenantOpen()) {
                            chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
                        }
                        List<Long> chargeDeptId = standardDocMapper.selectIdByChargeDeptName(chargeDeptName, tenantId);
                        if (CollectionUtils.isEmpty(chargeDeptId)) {
                            addErrorMsg(i, "未找到此责任部门，请检查数据");
                            return false;
                        }
                    }
                    //当sheet页”数据标准“中的分组名称在本次导入表格和系统中不存在时则不能导入，并提示”${分组}分组不存在“，并在对应单元格高亮警示
                    String groupName = standardDocDTO.getGroupName();
                    if (StringUtils.isEmpty(groupName)) {
                        addErrorMsg(i, String.format("表格中不存在分组%s", groupName));
                        return false;
                    }
                    List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                                    .andEqualTo(StandardGroup.FIELD_GROUP_NAME, groupName)
                                    .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, DOC)
                                    .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId)
                                    .andEqualTo(StandardGroup.FIELD_PROJECT_ID, projectId))
                            .build());
                    if (CollectionUtils.isEmpty(standardGroupDTOS)) {
                        addErrorMsg(i, String.format("导入环境中不存在分组%s", groupName));
                        return false;
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
