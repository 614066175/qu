package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
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
 * <p>
 * description
 * </p>
 *
 * @author wsl
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_FIELD)})
public class DataFieldValidator extends ValidatorHandler {
    private final ObjectMapper objectMapper;

    private final DataFieldRepository dataFieldRepository;

    private final StandardGroupRepository standardGroupRepository;

    public DataFieldValidator(ObjectMapper objectMapper, DataFieldRepository dataFieldRepository, StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.standardGroupRepository = standardGroupRepository;
    }


    @Override
    public boolean validate(String data) {
        DataFieldDTO dataFieldDTO;
        if (StringUtils.isNoneBlank(data)) {
            try {
                dataFieldDTO = objectMapper.readValue(data, DataFieldDTO.class);
                //导入数据标准分组是否存在
                Long tenantId = DetailsHelper.getUserDetails().getTenantId();
                List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataFieldDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId))
                        .build());
                //分组不存在
                if (CollectionUtils.isEmpty(standardGroupDTOS)) {
                    return false;
                }
                List<DataFieldDTO> dataFieldDTOList;
                dataFieldDTOList = dataFieldRepository.selectDTOByCondition(Condition.builder(DataField.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                                .andEqualTo(DataField.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准名称存在
                if (CollectionUtils.isNotEmpty(dataFieldDTOList)){
                    return false;
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}
