package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardTeamRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final DataStandardRepository dataStandardRepository;
    private final StandardTeamRepository standardTeamRepository;

    public DataFieldValidator(ObjectMapper objectMapper, DataFieldRepository dataFieldRepository, DataFieldMapper dataFieldMapper, DataStandardRepository dataStandardRepository, StandardTeamRepository standardTeamRepository) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.standardTeamRepository = standardTeamRepository;
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

                if(StringUtils.isEmpty(dataFieldDTO.getFieldType())){
                    addErrorMsg(i, "字段类型不能为空");
                    return false;
                }else {
                    if("DECIMAL".equals(dataFieldDTO.getFieldType() ) || "INTEGER".equals(dataFieldDTO.getFieldType())){
                        //校验字段精度
                        if(ObjectUtils.isNotEmpty(dataFieldDTO.getFieldAccuracy())){
                            //字段精度为正整数
                            if (!isNumeric(dataFieldDTO.getFieldAccuracy().toString())){
                                addErrorMsg(i,"浮点型字段精度需要为正整数");
                                return false;
                            }
                        }
                    }else {
                        if(ObjectUtils.isNotEmpty(dataFieldDTO.getFieldAccuracy())){
                            addErrorMsg(i,"非浮点型字段不应设置字段精度");
                            return false;
                        }
                    }
                }
                //校验目标环境字段标准组
                if (StringUtils.isNotEmpty(dataFieldDTO.getStandardTeamCode())) {
                    String[] standardTeamCodeList = dataFieldDTO.getStandardTeamCode().split(";");
                    for (String standardTeamCode : standardTeamCodeList) {
                        List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByCondition(Condition.builder(StandardTeam.class).andWhere(Sqls.custom()
                                        .andEqualTo(StandardTeam.FIELD_STANDARD_TEAM_CODE, standardTeamCode))
                                .build());
                        if (CollectionUtils.isNotEmpty(standardTeamDTOS)) {
                            Long standardTeamId = standardTeamDTOS.get(0).getStandardTeamId();
                            if (ObjectUtils.isEmpty(standardTeamId)) {
                                addErrorMsg(i, String.format("导入环境字段标准组：%s不存在", standardTeamCode));
                                return false;
                            }
                        }
                    }
                }

                //校验目标环境引用数据标准
                if(StringUtils.isNotEmpty(dataFieldDTO.getDataStandardCode())){
                    List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class).andWhere(Sqls.custom()
                                    .andEqualTo(DataStandard.FIELD_TENANT_ID,tenantId)
                                    .andEqualTo(DataStandard.FIELD_PROJECT_ID,projectId)
                                    .andEqualTo(DataStandard.FIELD_STANDARD_CODE,dataFieldDTO.getDataStandardCode()))
                            .build());
                    if(CollectionUtils.isEmpty(dataStandardDTOList)){
                        addErrorMsg(i, String.format("导入环境引用的数据标准：%s不存在", dataFieldDTO.getDataStandardCode()));
                        return false;
                    }
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }


    public static boolean isNumeric(String string){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }
}
