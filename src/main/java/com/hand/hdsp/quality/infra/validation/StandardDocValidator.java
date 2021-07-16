package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * @author StoneHell
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC)})
public class StandardDocValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;

    public StandardDocValidator(ObjectMapper objectMapper, StandardDocRepository standardDocRepository) {
        this.objectMapper = objectMapper;
        this.standardDocRepository = standardDocRepository;
    }


    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
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
                    if (!standardDocDTO.getStandardCode().matches("^[a-zA-Z][a-zA-Z0-9_]*$")){
                        addErrorMsg(i, "标准编码不符合:1-64个字符，可包含字母、数字或下划线”_”，英文字母开头");
                        return false;
                    }

                    //分组名称不符合规范
                    if (!standardDocDTO.getGroupName().matches("^[a-zA-Z][a-zA-Z0-9_]*$")){
                        addErrorMsg(i, "标准编码不符合:1-64个字符，可包含字母、数字或下划线”_”，英文字母开头");
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
