package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;

/**
 * <p>
 * description
 * </p>
 *
 * @author wsl
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD, sheetIndex = 1)})
public class DataFieldValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;

    private final DataFieldRepository dataFieldRepository;

    private final DataFieldMapper dataFieldMapper;

    public DataFieldValidator(ObjectMapper objectMapper, DataFieldRepository dataFieldRepository, DataFieldMapper dataFieldMapper) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.dataFieldMapper = dataFieldMapper;
    }

    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        for (int i = 0; i < data.size(); i++) {
            try {
                DataFieldDTO dataFieldDTO = objectMapper.readValue(data.get(i), DataFieldDTO.class);
                dataFieldDTO.setTenantId(tenantId);
                String fieldName = dataFieldDTO.getFieldName();
                if(StringUtils.isEmpty(fieldName)){
                    addErrorMsg(i,"导入表格中字段名称不存在");
                    return false;
                }
                List<DataFieldDTO> dataFieldDTOS = dataFieldRepository.selectDTOByCondition(Condition.builder(DataField.class).andWhere(Sqls.custom()
                                .andEqualTo(DataField.FIELD_FIELD_NAME, fieldName)
                                .andEqualTo(DataField.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(DataField.FIELD_PROJECT_ID, projectId)
                        )
                        .build());
                if(CollectionUtils.isNotEmpty(dataFieldDTOS)){
                    addErrorMsg(i,"标准字段名称：" + dataFieldDTO.getFieldName() + "已存在;");
                    return false;
                }
                //如果有责任人，则进行验证
                //校验的责任人名称为员工姓名
                if(DataSecurityHelper.isTenantOpen()){
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(dataFieldDTO.getChargeName());
                    dataFieldDTO.setChargeName(chargeName);
                }
                Long chargeId = dataFieldMapper.checkCharger(dataFieldDTO.getChargeName(), dataFieldDTO.getTenantId());
                if (ObjectUtils.isEmpty(chargeId)) {
                    addErrorMsg(i, "未找到此责任人，请检查数据");
                    return false;
                }
                //当sheet页”数据标准“中的“分组名称”字段在本次导入表格和系统中不存在时则不能导入，并提示”${分组}分组不存在“，并在对应单元格高亮警示
                String groupCode = dataFieldDTO.getGroupCode();
                if (StringUtils.isEmpty(groupCode)) {
                    addErrorMsg(i, String.format("表格中不存在分组%s", groupCode));
                    return false;
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
