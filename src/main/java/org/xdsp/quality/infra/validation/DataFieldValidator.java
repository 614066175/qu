package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.exception.JsonException;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.xdsp.core.profile.XdspProfileClient;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.api.dto.StandardTeamDTO;
import org.xdsp.quality.domain.entity.DataField;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.entity.StandardTeam;
import org.xdsp.quality.domain.repository.DataFieldRepository;
import org.xdsp.quality.domain.repository.DataStandardRepository;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.domain.repository.StandardTeamRepository;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.mapper.DataFieldMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.xdsp.quality.infra.constant.PlanConstant.StandardStatus.OFFLINE_APPROVING;
import static org.xdsp.quality.infra.constant.PlanConstant.StandardStatus.ONLINE;

import io.choerodon.core.oauth.DetailsHelper;

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
    private final ReferenceDataRepository referenceDataRepository;
    private final XdspProfileClient profileClient;
    private final LovAdapter lovAdapter;

    public DataFieldValidator(ObjectMapper objectMapper, DataFieldRepository dataFieldRepository, DataFieldMapper dataFieldMapper, DataStandardRepository dataStandardRepository, StandardTeamRepository standardTeamRepository, ReferenceDataRepository referenceDataRepository, XdspProfileClient profileClient, LovAdapter lovAdapter) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.standardTeamRepository = standardTeamRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.profileClient = profileClient;
        this.lovAdapter = lovAdapter;
    }

    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        for (int i = 0; i < data.size(); i++) {
            try {
                DataFieldDTO dataFieldDTO = objectMapper.readValue(data.get(i), DataFieldDTO.class);
                dataFieldDTO.setTenantId(tenantId);
                dataFieldDTO.setProjectId(projectId);

                String fieldName = dataFieldDTO.getFieldName();
                if (StringUtils.isEmpty(fieldName)) {
                    addErrorMsg(i, "导入表格中字段名称不存在");
                }
                List<DataFieldDTO> dataFieldDTOS = dataFieldRepository.selectDTOByCondition(Condition.builder(DataField.class).andWhere(Sqls.custom()
                                .andEqualTo(DataField.FIELD_FIELD_NAME, fieldName)
                                .andEqualTo(DataField.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(DataField.FIELD_PROJECT_ID, projectId)
                        )
                        .build());
                //检验是否允许调整
                if (CollectionUtils.isNotEmpty(dataFieldDTOS)) {
                    DataFieldDTO exist = dataFieldDTOS.get(0);
                    if (ONLINE.equals(exist.getStandardStatus()) || OFFLINE_APPROVING.equals(exist.getStandardStatus())) {
                        String offlineOpen = profileClient.getProfileValue(tenantId, projectId, WorkFlowConstant.OpenConfig.FIELD_STANDARD_OFFLINE);
                        if (offlineOpen == null || Boolean.parseBoolean(offlineOpen)) {
                            //如果需要下线审批,则报错
                            addErrorMsg(i, "标准已存在，状态不可进行数据修改，请先下线标准！");
                        }
                    }
                }
                // 验证责任人和责任部门
                validChargeInfo(dataFieldDTO, i);

                // 根据值域类型验证值域范围
                validaValueRange(dataFieldDTO, i);

                // 验证字段类型和精度
                validFiledTypeAndAccuracy(dataFieldDTO, i);

                //校验目标环境字段标准组
                validStandardTeam(dataFieldDTO, i);

                //校验目标环境引用数据标准
                validDataStandard(dataFieldDTO, tenantId, projectId, i);

                //检验附加信息key是否重复
                validExtraDuplicateKey(dataFieldDTO, i);
            } catch (IOException e) {
                log.info("DataField Validation Failed",e);
                addErrorMsg(i, e.getMessage());
            }
        }
        return true;
    }

    private void validStandardTeam(DataFieldDTO dataFieldDTO, int i) {
        if (StringUtils.isNotEmpty(dataFieldDTO.getStandardTeamCode())) {
            String[] standardTeamCodeList = dataFieldDTO.getStandardTeamCode().split(";");
            for (String standardTeamCode : standardTeamCodeList) {
                List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByCondition(Condition.builder(StandardTeam.class).andWhere(Sqls.custom()
                                .andEqualTo(StandardTeam.FIELD_STANDARD_TEAM_CODE, standardTeamCode))
                        .build());
                if (CollectionUtils.isEmpty(standardTeamDTOS)) {
                    addErrorMsg(i, String.format("导入环境字段标准组：%s不存在", standardTeamCode));
                }
            }
        }
    }

    private void validDataStandard(DataFieldDTO dataFieldDTO, Long tenantId, Long projectId, int i) {
        if (StringUtils.isNotEmpty(dataFieldDTO.getDataStandardCode())) {
            List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class).andWhere(Sqls.custom()
                            .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                            .andEqualTo(DataStandard.FIELD_PROJECT_ID, projectId)
                            .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataFieldDTO.getDataStandardCode()))
                    .build());
            if (CollectionUtils.isEmpty(dataStandardDTOList)) {
                addErrorMsg(i, String.format("导入环境引用的数据标准：%s不存在", dataFieldDTO.getDataStandardCode()));
            }
        }
    }

    private void validFiledTypeAndAccuracy(DataFieldDTO dataFieldDTO, int i) {
        if (StringUtils.isEmpty(dataFieldDTO.getFieldType())) {
            addErrorMsg(i, "字段类型不能为空");
        } else {
            if ("DECIMAL".equals(dataFieldDTO.getFieldType())) {
                //校验字段精度
                if (ObjectUtils.isNotEmpty(dataFieldDTO.getFieldAccuracy())) {
                    //字段精度为正整数
                    if (!isNumeric(dataFieldDTO.getFieldAccuracy().toString())) {
                        addErrorMsg(i, "浮点型字段精度需要为正整数");
                    }
                } else {
                    addErrorMsg(i, "浮点型字段需设置字段精度");
                }
            } else {
                if (ObjectUtils.isNotEmpty(dataFieldDTO.getFieldAccuracy())) {
                    addErrorMsg(i, "非浮点型字段无需设置字段精度");
                }
            }
        }
    }

    private void validExtraDuplicateKey(DataFieldDTO dataFieldDTO, int i) {
        String standardExtraStr = dataFieldDTO.getStandardExtraStr();
        Set<String> keyNames = new HashSet<>();
        if (StringUtils.isNotEmpty(standardExtraStr)) {
            try{
                List<Map<String, Object>> list = JsonUtil.toObj(standardExtraStr, List.class);
                for (Map<String, Object> map : list) {
                    String keyName = String.valueOf(map.keySet().iterator().next());
                    if (StringUtils.isEmpty(keyName)) {
                        addErrorMsg(i, "附加信息key不能为空");
                    }
                    if (keyNames.contains(keyName)) {
                        addErrorMsg(i, String.format("附加信息key【%s】重复", keyName));
                    }
                    keyNames.add(keyName);
                }
            }catch (JsonException e){
                log.error("Json Error",e);
                addErrorMsg(i,"JSON格式错误:"+e.getMessage());
            }
        }
    }

    private void validChargeInfo(DataFieldDTO dataFieldDTO, int i) {
        //如果有责任人，则进行验证
        if(StringUtils.isNotEmpty(dataFieldDTO.getChargeName())){
            //校验的责任人名称为员工姓名
            if (DataSecurityHelper.isTenantOpen()) {
                //加密后查询
                String chargeName = DataSecurityHelper.encrypt(dataFieldDTO.getChargeName());
                dataFieldDTO.setChargeName(chargeName);

            }
            List<Long> chargeIds = dataFieldMapper.checkCharger(dataFieldDTO.getChargeName(), dataFieldDTO.getTenantId());
            if (CollectionUtils.isEmpty(chargeIds)) {
                addErrorMsg(i, "未找到此责任人，请检查数据");
            }
        } else {
            addErrorMsg(i,"责任人为空");
        }

        //责任部门不为空进行校验
        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
            if(DataSecurityHelper.isTenantOpen()){
                String chargeDeptName = DataSecurityHelper.encrypt(dataFieldDTO.getChargeDeptName());
                dataFieldDTO.setChargeDeptName(chargeDeptName);
            }
            // 查询与责任人关联的责任部门
            List<Long> chargeDeptId = dataFieldMapper.selectIdByChargeAndDeptName(dataFieldDTO.getChargeName(), dataFieldDTO.getChargeDeptName());
            if (CollectionUtils.isEmpty(chargeDeptId)) {
                addErrorMsg(i, "未找到此部门，请检查数据");
            }
        }
    }

    private void validaValueRange(DataFieldDTO dataFieldDTO, int i) {
        if (StringUtils.isNotEmpty(dataFieldDTO.getValueType()) && StringUtils.isNotEmpty(dataFieldDTO.getValueRange())) {
            // 验证值域范围表达式
            switch (dataFieldDTO.getValueType()) {
                case PlanConstant.StandardValueType.AREA: {
                    // 根据值域类型获取对应正则，匹配至于范围表达式
                    Matcher matcher = PlanConstant.StandardValueType.ValueTypeRegexPattern.get(dataFieldDTO.getValueType())
                            .matcher(dataFieldDTO.getValueRange());
                    if (!matcher.find()) {
                        addErrorMsg(i, "值域范围表达式错误");
                    }
                }
                break;
                // 值集 或 值集视图
                case PlanConstant.StandardValueType.VALUE_SET:
                case PlanConstant.StandardValueType.LOV_VIEW: {
                    List<LovValueDTO> lovValueDTOS = lovAdapter.queryLovValue(dataFieldDTO.getValueRange(), dataFieldDTO.getTenantId());
                    if (CollectionUtils.isEmpty(lovValueDTOS)) {
                        addErrorMsg(i, "值集或值集视图不存在");
                    }
                }
                break;
                // 参考数据
                case PlanConstant.StandardValueType.REFERENCE_DATA: {
                    List<ReferenceData> referenceDataList = referenceDataRepository.select(ReferenceData
                            .builder()
                            .dataCode(dataFieldDTO.getValueRange())
                            .projectId(dataFieldDTO.getProjectId())
                            .tenantId(dataFieldDTO.getTenantId())
                            .build());
                    if (CollectionUtils.isEmpty(referenceDataList)) {
                        addErrorMsg(i, "参考数据不存在");
                    }
                }
                break;
                default: {
                    // 不处理
                }
            }
        }
    }


    public static boolean isNumeric(String string) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }
}
