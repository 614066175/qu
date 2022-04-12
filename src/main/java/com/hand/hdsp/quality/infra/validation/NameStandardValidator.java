package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import com.hand.hdsp.quality.infra.util.ImportUtil;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.List;

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
    private final StandardGroupRepository standardGroupRepository;
    private final ImportUtil importUtil;

    public NameStandardValidator(ObjectMapper objectMapper,
                                 NameStandardRepository nameStandardRepository,
                                 NameStandardMapper nameStandardMapper,
                                 StandardGroupRepository standardGroupRepository, ImportUtil importUtil) {
        this.objectMapper = objectMapper;
        this.nameStandardRepository = nameStandardRepository;
        this.nameStandardMapper = nameStandardMapper;
        this.standardGroupRepository = standardGroupRepository;
        this.importUtil = importUtil;
    }

    @Override
    public boolean validate(String data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        NameStandardDTO nameStandardDTO;
        try{
            nameStandardDTO=objectMapper.readValue(data, NameStandardDTO.class);
            nameStandardDTO.setTenantId(tenantId);
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
        try{
            importUtil.getChargerId(nameStandardDTO.getChargeName(),nameStandardDTO.getTenantId());
        }catch (Exception e){
            addErrorMsg("未找到此责任人，请检查数据");
            return false;
        }

        //如果责任部门不为空时进行检验
        if (Strings.isNotEmpty(nameStandardDTO.getChargeDeptName())) {
            try{
                importUtil.getChargeDeptId(nameStandardDTO.getChargeDeptName(),nameStandardDTO.getTenantId());
            }catch (Exception e){
                addErrorMsg("未找到此责任部门，请检查数据");
                return false;
            }
        }
        return true;
    }
}
