package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.boot.platform.profile.ProfileClient;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.exception.JsonException;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.domain.repository.DataStandardRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.mapper.DataStandardMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.xdsp.quality.infra.constant.PlanConstant.StandardStatus.OFFLINE_APPROVING;
import static org.xdsp.quality.infra.constant.PlanConstant.StandardStatus.ONLINE;

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
    private final ObjectMapper objectMapper;

    private final DataStandardRepository dataStandardRepository;

    private final DataStandardMapper dataStandardMapper;
    private final CommonGroupRepository commonGroupRepository;

    @Autowired
    private ProfileClient profileClient;

    public DataStandardValidator(ObjectMapper objectMapper,
                                 DataStandardRepository dataStandardRepository,
                                 DataStandardMapper dataStandardMapper,
                                 CommonGroupRepository commonGroupRepository) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.commonGroupRepository = commonGroupRepository;
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

                //校验的责任人名称为员工姓名
                if (DataSecurityHelper.isTenantOpen()) {
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(dataStandardDTO.getChargeName());
                    dataStandardDTO.setChargeName(chargeName);
                }
                Long chargeId = dataStandardMapper.checkCharger(dataStandardDTO.getChargeName(), dataStandardDTO.getTenantId());
                if (ObjectUtils.isEmpty(chargeId)) {
                    addErrorMsg(i, "未找到此责任人，请检查数据");
                }
                //如果责任部门不为空时进行检验
                if (Strings.isNotEmpty(dataStandardDTO.getChargeDeptName())) {
                    String chargeDeptName = dataStandardDTO.getChargeDeptName();
                    if (DataSecurityHelper.isTenantOpen()) {
                        chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
                    }
                    List<Long> chargeDeptId = dataStandardMapper.selectIdByChargeDeptName(chargeDeptName, dataStandardDTO.getTenantId());
                    if (CollectionUtils.isEmpty(chargeDeptId)) {
                        addErrorMsg(i, "未找到此责任人，请检查数据");
                    }
                }

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
                        String offlineOpen = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(), null, null, WorkFlowConstant.OpenConfig.DATA_STANDARD_OFFLINE);
                        if (offlineOpen == null || Boolean.parseBoolean(offlineOpen)) {
                            //如果需要下线审批,则报错
                            addErrorMsg(i, "标准已存在，状态不可进行数据修改，请先下线标准！");
                        }
                    }

                }
                //检验附加信息key是否重复
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
                        addErrorMsg(i, "JSON格式错误:"+e.getMessage());
                    }
                }
            } catch (IOException e) {
                log.error("DataStandard Validation Failed",e);
                addErrorMsg(i,e.getMessage());
            }
        }
        return true;
    }
}
