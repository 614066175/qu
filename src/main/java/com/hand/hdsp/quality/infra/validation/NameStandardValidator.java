package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * description
 * 命名标准校验逻辑
 * @author 29713 2021/08/26 14:13
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD)})
public class NameStandardValidator extends ValidatorHandler {

    private final ObjectMapper objectMapper;
    private final NameStandardRepository nameStandardRepository;
    private final NameStandardMapper nameStandardMapper;

    public NameStandardValidator(ObjectMapper objectMapper, NameStandardRepository nameStandardRepository, NameStandardMapper nameStandardMapper) {
        this.objectMapper = objectMapper;
        this.nameStandardRepository = nameStandardRepository;
        this.nameStandardMapper = nameStandardMapper;
    }

    @Override
    public boolean validate(String data) {
        NameStandardDTO nameStandardDTO;
        try{
            nameStandardDTO=objectMapper.readValue(data, NameStandardDTO.class);
        }catch (IOException e) {
            log.error("data:{}", data);
            log.error("Read Json Error", e);
            // 失败
            return false;
        }
        List<NameStandardDTO> nameStandards = nameStandardRepository.selectDTOByCondition(Condition
                .builder(NameStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(NameStandard.FIELD_STANDARD_CODE, nameStandardDTO.getStandardCode())
                        .andEqualTo(NameStandard.FIELD_TENANT_ID, nameStandardDTO.getTenantId()))
                .build());
        if(CollectionUtils.isNotEmpty(nameStandards)){
            getContext().addErrorMsg("该标准编码已存在！");
            return false;
        }
        Long groupId = nameStandardMapper.getGroupId(nameStandardDTO.getGroupCode());
        if(!Objects.isNull(groupId)){
           getContext().addErrorMsg("该分组编码不存在！");
        }
        return true;
    }
}
