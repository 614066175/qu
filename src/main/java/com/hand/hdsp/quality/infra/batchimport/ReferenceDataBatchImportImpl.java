package com.hand.hdsp.quality.infra.batchimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.infra.constant.CommonGroupConstants;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRepository;
import com.hand.hdsp.quality.infra.constant.ReferenceDataConstant;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.converter.ReferenceDataConverter;
import com.hand.hdsp.quality.infra.mapper.ReferenceDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * 参考数据头数据导入
 * @author fuqiang.luo@hand-china.com
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_REFERENCE_DATA, sheetIndex = 1)
public class ReferenceDataBatchImportImpl extends BatchImportHandler implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final ReferenceDataRepository referenceDataRepository;
    private final CommonGroupRepository commonGroupRepository;
    private final ReferenceDataMapper referenceDataMapper;
    private final ReferenceDataConverter referenceDataConverter;

    public ReferenceDataBatchImportImpl(ObjectMapper objectMapper,
                                        ReferenceDataRepository referenceDataRepository,
                                        CommonGroupRepository commonGroupRepository,
                                        ReferenceDataMapper referenceDataMapper,
                                        ReferenceDataConverter referenceDataConverter) {
        this.objectMapper = objectMapper;
        this.referenceDataRepository = referenceDataRepository;
        this.commonGroupRepository = commonGroupRepository;
        this.referenceDataMapper = referenceDataMapper;
        this.referenceDataConverter = referenceDataConverter;
    }


    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<ReferenceData> insertList = new ArrayList<>();
        List<ReferenceData> updateList = new ArrayList<>();
        try {
            for (int i = 0; i < data.size(); i++) {
                ReferenceDataDTO referenceDataDTO = objectMapper.readValue(data.get(i), ReferenceDataDTO.class);
                referenceDataDTO.setProjectId(projectId);
                referenceDataDTO.setTenantId(tenantId);
                referenceDataDTO.setDataStatus(ReferenceDataConstant.NEW);
                List<ReferenceData> referenceDataList = referenceDataRepository.select(ReferenceData
                        .builder()
                        .dataCode(referenceDataDTO.getDataCode())
                        .projectId(projectId)
                        .tenantId(tenantId)
                        .build());
                ReferenceData exist = null;
                if (CollectionUtils.isNotEmpty(referenceDataList)) {
                    // 已经存在的情况
                    exist = referenceDataList.get(0);
                    if (ReferenceDataConstant.RELEASED.equals(exist.getDataStatus())) {
                        log.error("参考数据(编码[{}])已经存在且已上线", exist.getDataCode());
                        addErrorMsg(i, "参考数据已经存在且已上线");
                        getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
                        continue;
                    }
                }
                // 获取分组id
                CommonGroup commonGroup = commonGroupRepository.selectOne(CommonGroup
                        .builder()
                        .groupType(CommonGroupConstants.GroupType.REFERENCE_DATA)
                        .groupPath(referenceDataDTO.getGroupPath())
                        .projectId(projectId)
                        .tenantId(tenantId)
                        .build());
                if (Objects.nonNull(commonGroup) && Objects.nonNull(commonGroup.getGroupId())) {
                    referenceDataDTO.setDataGroupId(commonGroup.getGroupId());
                }

                // 父参考数据id
                String parentDataCode = referenceDataDTO.getParentDataCode();
                if (StringUtils.isNotBlank(parentDataCode)) {
                    List<ReferenceData> parentReferenceDataList = referenceDataRepository.select(ReferenceData
                            .builder()
                            .dataCode(parentDataCode)
                            .projectId(projectId)
                            .tenantId(tenantId)
                            .build());
                    if (CollectionUtils.isNotEmpty(parentReferenceDataList)) {
                        referenceDataDTO.setParentDataId(parentReferenceDataList.get(0).getDataId());
                    }
                }
                // 获取责任部门ID
                String responsibleDeptName = referenceDataDTO.getResponsibleDeptName();
                if (StringUtils.isNotBlank(responsibleDeptName)) {
                    String deptName = responsibleDeptName;
                    if (DataSecurityHelper.isTenantOpen()) {
                        deptName = DataSecurityHelper.encrypt(responsibleDeptName);
                    }
                    List<Long> deptIds = referenceDataMapper.queryDeptIdsByName(deptName, tenantId);
                    if (CollectionUtils.isNotEmpty(deptIds)) {
                        referenceDataDTO.setResponsibleDeptId(deptIds.get(0));
                    }
                }
                // 获取责任人ID
                String responsiblePersonName = referenceDataDTO.getResponsiblePersonName();
                String employeeName = responsiblePersonName;
                if (DataSecurityHelper.isTenantOpen()) {
                    employeeName = DataSecurityHelper.encrypt(responsiblePersonName);
                }
                List<Long> employeeIds = referenceDataMapper.queryEmployeeIdsByName(employeeName, tenantId);
                if (CollectionUtils.isNotEmpty(employeeIds)) {
                    referenceDataDTO.setResponsiblePersonId(employeeIds.get(0));
                }
                if (Objects.nonNull(exist)) {
                    referenceDataDTO.setDataStatus(exist.getDataStatus());
                    referenceDataDTO.setDataId(exist.getDataId());
                    referenceDataDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    referenceDataDTO.setReleaseBy(exist.getReleaseBy());
                    referenceDataDTO.setReleaseDate(exist.getReleaseDate());
                    updateList.add(referenceDataConverter.dtoToEntity(referenceDataDTO));
                } else {
                    insertList.add(referenceDataConverter.dtoToEntity(referenceDataDTO));
                }
            }
            referenceDataRepository.batchUpdateByPrimaryKey(updateList);
            referenceDataRepository.batchInsertSelective(insertList);
        } catch (Exception e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        return true;
    }
}