package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
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
 * @author lgl 2020/12/04 16:06
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLAT_CODE_DATA_STANDARD)})
public class DataStandardValidator extends ValidatorHandler {
    private final ObjectMapper objectMapper;

    private final DataStandardRepository dataStandardRepository;

    private final StandardGroupRepository standardGroupRepository;

    public DataStandardValidator(ObjectMapper objectMapper, DataStandardRepository dataStandardRepository, StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.standardGroupRepository = standardGroupRepository;
    }


    @Override
    public boolean validate(String data) {
        DataStandardDTO dataStandardDTO;
        if (StringUtils.isNoneBlank(data)) {
            try {
                dataStandardDTO = objectMapper.readValue(data, DataStandardDTO.class);
                //导入数据标准分组是否存在
                Long tenantId = DetailsHelper.getUserDetails().getTenantId();
                List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataStandardDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId))
                        .build());
                //分组不存在
                if (CollectionUtils.isEmpty(standardGroupDTOS)) {
                    return false;
                }
                List<DataStandardDTO> dataStandardDTOList;
                dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准编码存在
                if (CollectionUtils.isNotEmpty(dataStandardDTOList)){
                    return false;
                }
                dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准名称存在
                if (CollectionUtils.isNotEmpty(dataStandardDTOList)){
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
