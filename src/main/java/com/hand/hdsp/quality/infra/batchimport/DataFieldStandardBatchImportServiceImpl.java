package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.transaction.annotation.Transactional;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.CREATE;

/**
 * @author wsl
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD, sheetIndex = 1)
public class DataFieldStandardBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final DataFieldRepository dataFieldRepository;
    private final StandardGroupRepository standardGroupRepository;
    private final DataFieldMapper dataFieldMapper;
    private final DataStandardRepository dataStandardRepository;
    private final StandardRelationRepository standardRelationRepository;
    private final StandardTeamRepository standardTeamRepository;

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
        List<DataFieldDTO> dataFieldDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                DataFieldDTO dataFieldDTO = objectMapper.readValue(json, DataFieldDTO.class);
                //导入分组id
                String groupCode = dataFieldDTO.getGroupCode();
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, groupCode)
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID, projectId)
                ).build());
                if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
                    dataFieldDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
                }
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
                dataFieldRepository.insertDTOSelective(dataFieldDTO);
                //设置字段标准组
                if (StringUtils.isNotEmpty(dataFieldDTO.getStandardTeamCode())) {
                    String[] standardTeamCodeList = dataFieldDTO.getStandardTeamCode().split(",");
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
