package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * @author StoneHell
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC)})
public class StandardDocValidator extends ValidatorHandler {
    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;
    private final StandardGroupRepository standardGroupRepository;

    public StandardDocValidator(ObjectMapper objectMapper, StandardDocRepository standardDocRepository,
                                StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.standardDocRepository = standardDocRepository;
        this.standardGroupRepository = standardGroupRepository;
    }


    @Override
    public boolean validate(String data) {
        StandardDocDTO standardDocDTO;
        if (StringUtils.isNoneBlank(data)) {
            try {
                standardDocDTO = objectMapper.readValue(data, StandardDocDTO.class);
                //导入数据标准分组是否存在
                Long tenantId = DetailsHelper.getUserDetails().getTenantId();
                List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, standardDocDTO.getGroupId())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId))
                        .build());
                //分组不存在
                if (CollectionUtils.isEmpty(standardGroupDTOS)) {
                    return false;
                }
                List<StandardDocDTO> standardDocDTOList;
                standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardDoc.FIELD_STANDARD_CODE, standardDocDTO.getStandardCode())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准编码存在
                if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
                    return false;
                }
                standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardCode())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准名称存在
                return !CollectionUtils.isNotEmpty(standardDocDTOList);
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}
