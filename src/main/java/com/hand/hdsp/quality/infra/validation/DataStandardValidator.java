package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
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
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_STANDARD)})
public class DataStandardValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;

    private final DataStandardRepository dataStandardRepository;

    private final DataStandardMapper dataStandardMapper;

    public DataStandardValidator(ObjectMapper objectMapper,
                                 DataStandardRepository dataStandardRepository,
                                 DataStandardMapper dataStandardMapper) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardMapper = dataStandardMapper;
    }


    @Override
    public boolean validate(List<String> data) {
        List<DataStandardDTO> dataStandardDTOList = new ArrayList<>(data.size());
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        try {
            for (int i = 0; i < data.size(); i++) {
                DataStandardDTO dataStandardDTO = objectMapper.readValue(data.get(i), DataStandardDTO.class);
                dataStandardDTO.setTenantId(tenantId);
                dataStandardDTOList.add(dataStandardDTO);
                dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准编码存在
                if (CollectionUtils.isNotEmpty(dataStandardDTOList)) {
                    addErrorMsg(i, "标准编码已存在");
                    return false;
                }
                dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardName())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准名称存在
                if (CollectionUtils.isNotEmpty(dataStandardDTOList)) {
                    addErrorMsg(i, "标准名称已存在");
                    return false;
                }
                List<Long> chargeId = dataStandardMapper.selectIdByChargeName(dataStandardDTO.getChargeName(),
                        dataStandardDTO.getTenantId());
                if(CollectionUtils.isEmpty(chargeId)){
                    addErrorMsg(i,"未找到此责任人，请检查数据");
                    return false;
                }
                //如果责任部门不为空时进行检验
                if (Strings.isNotEmpty(dataStandardDTO.getChargeDeptName())) {
                    String chargeDeptName = dataStandardDTO.getChargeDeptName();
                    if (DataSecurityHelper.isTenantOpen()) {
                        chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
                    }
                    List<Long> chargeDeptId = dataStandardMapper.selectIdByChargeDeptName(chargeDeptName, dataStandardDTO.getTenantId());
                    if (CollectionUtils.isEmpty(chargeDeptId)) {
                        addErrorMsg(i,"未找到此责任人，请检查数据");
                        return false;
                    }
                }
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
