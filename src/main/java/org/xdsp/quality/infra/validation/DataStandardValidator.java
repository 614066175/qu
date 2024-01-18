package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.xdsp.core.profile.XdspProfileClient;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.repository.DataStandardRepository;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.mapper.DataStandardMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static org.xdsp.quality.infra.constant.PlanConstant.StandardStatus.OFFLINE_APPROVING;
import static org.xdsp.quality.infra.constant.PlanConstant.StandardStatus.ONLINE;

import io.choerodon.core.oauth.DetailsHelper;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/04 16:06
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_STANDARD, sheetIndex = 1)})
public class DataStandardValidator extends BatchValidatorHandler {
    private final DataStandardRepository dataStandardRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final ObjectMapper objectMapper;
    private final DataStandardMapper dataStandardMapper;

    private final LovAdapter lovAdapter;

    @Autowired
    private XdspProfileClient profileClient;

    public DataStandardValidator(ObjectMapper objectMapper,
                                 DataStandardRepository dataStandardRepository, ReferenceDataRepository referenceDataRepository,
                                 DataStandardMapper dataStandardMapper, LovAdapter lovAdapter) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.lovAdapter = lovAdapter;
    }


    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        for (int i = 0; i < data.size(); i++) {
            try {
                DataStandardDTO dataStandardDTO = objectMapper.readValue(data.get(i), DataStandardDTO.class);
                dataStandardDTO.setTenantId(tenantId);
                dataStandardDTO.setProjectId(projectId);

                // 校验责任人和责任部门
                validChargeInfo(dataStandardDTO, i);

                // 校验数据标准编码和状态
                validStandardCodeAndStatus(dataStandardDTO, tenantId, projectId, i);

                // 根据值域类型校验值域范围
                validaValueRange(dataStandardDTO, i);

                //检验附加信息key是否重复
                validExtraDuplicateKey(dataStandardDTO, i);
            } catch (IOException e) {
                log.error("DataStandard Validation Failed", e);
                addErrorMsg(i, e.getMessage());
            }
        }
        return true;
    }


    //===============================================================================
    //  详细校验逻辑
    //===============================================================================

    private void validaValueRange(DataStandardDTO dataStandardDTO, int i) {
        if (StringUtils.isNotEmpty(dataStandardDTO.getValueType()) && StringUtils.isNotEmpty(dataStandardDTO.getValueRange())) {
            // 验证值域范围表达式
            switch (dataStandardDTO.getValueType()) {
                case PlanConstant.StandardValueType.AREA: {
                    // 根据值域类型获取对应正则，匹配至于范围表达式
                    Matcher matcher = PlanConstant.StandardValueType.ValueTypeRegexPattern.get(dataStandardDTO.getValueType())
                            .matcher(dataStandardDTO.getValueRange());
                    if (!matcher.find()) {
                        addErrorMsg(i, "值域范围表达式错误");
                    }
                }
                break;
                // 值集 或 值集视图
                case PlanConstant.StandardValueType.VALUE_SET:
                case PlanConstant.StandardValueType.LOV_VIEW: {
                    List<LovValueDTO> lovValueDTOS = lovAdapter.queryLovValue(dataStandardDTO.getValueRange(), dataStandardDTO.getTenantId());
                    if (CollectionUtils.isEmpty(lovValueDTOS)) {
                        addErrorMsg(i, "值集或值集视图不存在");
                    }
                }
                break;
                // 参考数据
                case PlanConstant.StandardValueType.REFERENCE_DATA: {
                    List<ReferenceData> referenceDataList = referenceDataRepository.select(ReferenceData
                            .builder()
                            .dataCode(dataStandardDTO.getValueRange())
                            .projectId(dataStandardDTO.getProjectId())
                            .tenantId(dataStandardDTO.getTenantId())
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

    private void validExtraDuplicateKey(DataStandardDTO dataStandardDTO, int i) {
        String standardExtraStr = dataStandardDTO.getStandardExtraStr();
        Set<String> keyNames = new HashSet<>();
        if (StringUtils.isNotEmpty(standardExtraStr)) {
            try {
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
            } catch (JsonException e) {
                log.error("Json Error", e);
                addErrorMsg(i, "JSON格式错误:" + e.getMessage());
            }
        }
    }

    private void validStandardCodeAndStatus(DataStandardDTO dataStandardDTO, Long tenantId, Long projectId, int i) {
        List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardDTO.getStandardCode())
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DataStandard.FIELD_PROJECT_ID, projectId))
                .build());
        //标准编码存在
        //若编码已存在，且状态为新建/离线，则采用更新逻辑。
        //若编码已存在，且流程不需要下线审批，状态为在线/下线审批中，则下线原内容后更新。
        //若编码已存在，且流程需要下线审批，状态为在线/下线审批中，则报错。
        //若编码已存在，状态为发布审核中，则撤回之前的流程后更新。
        if (CollectionUtils.isNotEmpty(dataStandardDTOList)) {
            DataStandardDTO exist = dataStandardDTOList.get(0);
            if (ONLINE.equals(exist.getStandardStatus()) || OFFLINE_APPROVING.equals(exist.getStandardStatus())) {
                String offlineOpen = profileClient.getProfileValue(tenantId, projectId, WorkFlowConstant.OpenConfig.DATA_STANDARD_OFFLINE);
                if (offlineOpen == null || Boolean.parseBoolean(offlineOpen)) {
                    //如果需要下线审批,则报错
                    addErrorMsg(i, "标准已存在，状态不可进行数据修改，请先下线标准！");
                }
            }

        }
    }

    private void validChargeInfo(DataStandardDTO dataStandardDTO, int i) {
        // 责任人校验
        if (StringUtils.isNotEmpty(dataStandardDTO.getChargeName())) {
            //校验的责任人名称为员工姓名
            if (DataSecurityHelper.isTenantOpen()) {
                //加密后查询
                String chargeName = DataSecurityHelper.encrypt(dataStandardDTO.getChargeName());
                dataStandardDTO.setChargeName(chargeName);
            }
            List<Long> chargeIds = dataStandardMapper.checkCharger(dataStandardDTO.getChargeName(), dataStandardDTO.getTenantId());
            if (CollectionUtils.isEmpty(chargeIds)) {
                addErrorMsg(i, "未找到此责任人，请检查数据");
            }
        } else {
            addErrorMsg(i, "责任人为空");
        }

        //如果责任部门不为空时进行检验
        if (Strings.isNotEmpty(dataStandardDTO.getChargeDeptName())) {
            String chargeDeptName = dataStandardDTO.getChargeDeptName();
            if (DataSecurityHelper.isTenantOpen()) {
                chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
            }
            // 查询与责任人关联的责任部门
            List<Long> chargeDeptId = dataStandardMapper.selectIdByChargeAndDeptName(dataStandardDTO.getChargeName(), chargeDeptName);
            if (CollectionUtils.isEmpty(chargeDeptId)) {
                addErrorMsg(i, "未找到此责任部门，请检查数据");
            }
        }
    }
}
