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
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
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
                //判断导入数据的负责人租户是不是和当前登录用户租户相同
                Long chargeTenantId = dataStandardMapper.selectTenantIdByChargeName(dataStandardDTO.getChargeName());
                if (tenantId.compareTo(chargeTenantId) != 0) {
                    addErrorMsg(i, "负责人租户ID和当前租户ID不一致");
                    return false;
                }
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
                                .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardCode())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准名称存在
                if (CollectionUtils.isNotEmpty(dataStandardDTOList)) {
                    addErrorMsg(i, "标准名称已存在");
                    return false;
                }
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
