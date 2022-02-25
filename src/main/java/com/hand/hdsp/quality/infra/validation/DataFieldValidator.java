package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * description
 * </p>
 *
 * @author wsl
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD)})
public class DataFieldValidator extends ValidatorHandler {
    private final ObjectMapper objectMapper;

    private final DataFieldRepository dataFieldRepository;

    private final DataFieldMapper dataFieldMapper;

    public DataFieldValidator(ObjectMapper objectMapper, DataFieldRepository dataFieldRepository, StandardGroupRepository standardGroupRepository, DataFieldMapper dataFieldMapper) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.dataFieldMapper = dataFieldMapper;
    }


    /**
     * 对标准字段名称作校验
     *
     * @param data DataFieldDTO的json格式
     * @return 字段验证是否成功
     */
    @Override
    public boolean validate(String data) {
        if (StringUtils.isEmpty(data)) {
            return false;
        }
        DataFieldDTO dataFieldDTO;
        try {
            dataFieldDTO = objectMapper.readValue(data, DataFieldDTO.class);
        } catch (IOException e) {
            addErrorMsg(e.getMessage());
            log.info(e.getMessage());
            return false;
        }
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Condition condition = Condition.builder(DataField.class).andWhere(Sqls.custom()
                .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                .andEqualTo(DataField.FIELD_TENANT_ID, tenantId)
        ).build();
        List<DataFieldDTO> dataFieldDTOList = dataFieldRepository.selectDTOByCondition(condition);
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            addErrorMsg("标准字段名称：" + dataFieldDTO.getFieldName() + "已存在;");
            return false;
        }
        //如果有责任人部门，则进行验证
        if(Strings.isNotEmpty(dataFieldDTO.getChargeDeptName())){
            String chargeDeptName = dataFieldDTO.getChargeDeptName();
            if(DataSecurityHelper.isTenantOpen()){
                chargeDeptName=DataSecurityHelper.encrypt(chargeDeptName);
            }
            List<Long> chargeDeptId = dataFieldMapper.selectIdByChargeDeptName(chargeDeptName,tenantId);
            if(CollectionUtils.isEmpty(chargeDeptId)){
                addErrorMsg("未找到此责任部门，请检查数据");
                return false;
            }
        }
        List<Long> chargeId = dataFieldMapper.selectIdByChargeName(dataFieldDTO.getChargeName(), dataFieldDTO.getTenantId());
        if(CollectionUtils.isEmpty(chargeId)){
            addErrorMsg("未找到此责任人，请检查数据");
            return false;
        }
        return true;
    }
}
