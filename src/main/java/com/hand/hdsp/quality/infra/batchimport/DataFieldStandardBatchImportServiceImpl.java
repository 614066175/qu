package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.CommonGroupClient;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardRelationDTO;
import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.boot.platform.profile.ProfileClient;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.DATA_STANDARD;
import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.FIELD_STANDARD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

/**
 * @author wsl
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD, sheetIndex = 1)
public class DataFieldStandardBatchImportServiceImpl extends BatchImportHandler implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final DataFieldRepository dataFieldRepository;
    private final StandardGroupRepository standardGroupRepository;
    private final DataFieldMapper dataFieldMapper;
    private final DataStandardRepository dataStandardRepository;
    private final StandardRelationRepository standardRelationRepository;
    private final StandardTeamRepository standardTeamRepository;

    @Autowired
    private CommonGroupRepository commonGroupRepository;
    @Autowired
    private ProfileClient profileClient;

    @Autowired
    private CommonGroupClient commonGroupClient;

    public DataFieldStandardBatchImportServiceImpl(ObjectMapper objectMapper,
                                                   DataFieldRepository dataFieldRepository,
                                                   StandardGroupRepository standardGroupRepository,
                                                   DataFieldMapper dataFieldMapper,
                                                   DataStandardRepository dataStandardRepository,
                                                   StandardRelationRepository standardRelationRepository,
                                                   StandardTeamRepository standardTeamRepository) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.standardGroupRepository = standardGroupRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.standardRelationRepository = standardRelationRepository;
        this.standardTeamRepository = standardTeamRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (int i = 0; i < data.size(); i++) {
                String json = data.get(i);
                DataFieldDTO dataFieldDTO = objectMapper.readValue(json, DataFieldDTO.class);
                //导入分组id
                CommonGroup commonGroup = commonGroupRepository.selectOne(CommonGroup.builder()
                        .groupType(FIELD_STANDARD)
                        .groupPath(dataFieldDTO.getGroupPath())
                        .tenantId(tenantId).projectId(projectId).build());
                if (commonGroup == null) {
                    //不存在直接新建
                    commonGroupClient.createGroup(tenantId, projectId, FIELD_STANDARD, dataFieldDTO.getGroupPath());
                    CommonGroup group = commonGroupRepository.selectOne(CommonGroup.builder()
                            .groupType(FIELD_STANDARD)
                            .groupPath(dataFieldDTO.getGroupPath())
                            .tenantId(tenantId).projectId(projectId).build());
                    dataFieldDTO.setGroupId(group.getGroupId());
                } else {
                    dataFieldDTO.setGroupId(commonGroup.getGroupId());
                }

                //标准编码存在
                //若编码已存在，且状态为新建/离线，则采用更新逻辑。
                //若编码已存在，且流程不需要下线审批，状态为在线/下线审批中，则下线原内容后更新。
                //若编码已存在，且流程需要下线审批，状态为在线/下线审批中，则报错。
                //若编码已存在，状态为发布审核中，则撤回之前的流程后更新。
                //导入更新后直接进入审核/在线状态
                dataFieldDTO.setTenantId(tenantId);
                dataFieldDTO.setProjectId(projectId);
                dataFieldDTO.setStandardStatus(CREATE);
                //设置责任人
                if (DataSecurityHelper.isTenantOpen()) {
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(dataFieldDTO.getChargeName());
                    dataFieldDTO.setChargeName(chargeName);
                }
                Long chargeId = dataFieldMapper.checkCharger(dataFieldDTO.getChargeName(), dataFieldDTO.getTenantId());
                dataFieldDTO.setChargeId(chargeId);
                //导入责任部门
                if (StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
                    String chargeDeptName = dataFieldDTO.getChargeDeptName();
                    if (DataSecurityHelper.isTenantOpen()) {
                        chargeDeptName = DataSecurityHelper.encrypt(dataFieldDTO.getChargeDeptName());
                    }
                    List<Long> chargeDeptId = dataFieldMapper.selectIdByChargeDeptName(chargeDeptName, dataFieldDTO.getTenantId());
                    if (CollectionUtils.isNotEmpty(chargeDeptId)) {
                        dataFieldDTO.setChargeDeptId(chargeDeptId.get(0));
                    }
                }
                //设置目标环境引用数据标准
                if (StringUtils.isNotEmpty(dataFieldDTO.getDataStandardCode())) {
                    List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class).andWhere(Sqls.custom()
                                    .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                                    .andEqualTo(DataStandard.FIELD_PROJECT_ID, projectId)
                                    .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataFieldDTO.getDataStandardCode()))
                            .build());
                    if (CollectionUtils.isNotEmpty(dataStandardDTOList)) {
                        dataFieldDTO.setDataStandardId(dataStandardDTOList.get(0).getStandardId());
                    }
                }

                //判断字段标准是否存在
                DataField exist = dataFieldRepository.selectOne(DataField.builder()
                        .fieldName(dataFieldDTO.getFieldName())
                        .tenantId(dataFieldDTO.getTenantId())
                        .projectId(dataFieldDTO.getProjectId())
                        .build());
                if (exist != null) {
                    dataFieldDTO.setFieldId(exist.getFieldId());
                    dataFieldDTO.setReleaseBy(exist.getReleaseBy());
                    dataFieldDTO.setReleaseDate(exist.getReleaseDate());
                    dataFieldDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    //存在默认使用本身状态
                    dataFieldDTO.setStandardStatus(exist.getStandardStatus());
                    //如果是在线或下线中状态，判断是否需要下线审核，需要则报错
                    if (ONLINE.equals(exist.getStandardStatus()) || OFFLINE_APPROVING.equals(exist.getStandardStatus())) {
                        String offlineOpen = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(), null, null, WorkFlowConstant.OpenConfig.FIELD_STANDARD_OFFLINE);
                        //为空，或者为true
                        if (StringUtils.isEmpty(offlineOpen) || Boolean.parseBoolean(offlineOpen)) {
                            addErrorMsg(i, "标准已存在，状态不可进行数据修改，请先下线标准！");
                            continue;
                        } else {
                            //设置为离线状态
                            dataFieldDTO.setStandardStatus(OFFLINE);
                        }
                    }
                    //更新
                    dataFieldRepository.updateDTOAllColumnWhereTenant(dataFieldDTO, dataFieldDTO.getTenantId());
                } else {
                    //不存在的话插入字段标准
                    dataFieldRepository.insertDTOSelective(dataFieldDTO);
                }

                //设置字段标准组
                if (StringUtils.isNotEmpty(dataFieldDTO.getStandardTeamCode())) {
                    String[] standardTeamCodeList = dataFieldDTO.getStandardTeamCode().split(";");
                    Long fieldId = dataFieldDTO.getFieldId();
                    for (String standardTeamCode : standardTeamCodeList) {
                        List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByCondition(Condition.builder(StandardTeam.class).andWhere(Sqls.custom()
                                        .andEqualTo(StandardTeam.FIELD_STANDARD_TEAM_CODE, standardTeamCode))
                                .build());
                        if (CollectionUtils.isNotEmpty(standardTeamDTOS)) {
                            Long standardTeamId = standardTeamDTOS.get(0).getStandardTeamId();
                            if (ObjectUtils.isNotEmpty(standardTeamId)) {
                                //关联导入环境 - 字段标准组，字段标准
                                standardRelationRepository.insertDTOSelective(StandardRelationDTO.builder()
                                        .tenantId(tenantId)
                                        .projectId(projectId)
                                        .standardTeamId(standardTeamId)
                                        .fieldStandardId(fieldId)
                                        .build());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // 失败
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            return false;
        }
        return true;
    }
}
