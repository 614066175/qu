package org.xdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.core.infra.constant.CommonGroupConstants;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.ReferenceDataDTO;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.infra.constant.ReferenceDataConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.converter.ReferenceDataConverter;
import org.xdsp.quality.infra.mapper.ReferenceDataMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        boolean error = false;
        for (int i = 0; i < data.size(); i++) {
            try {
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
                } else {
                    addErrorMsg(i, String.format("分组[%s]不存在", referenceDataDTO.getGroupPath()));
                    getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
                    continue;
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
                    referenceDataRepository.updateByDTOPrimaryKey(referenceDataDTO);
                } else {
                    referenceDataRepository.insertDTOSelective(referenceDataDTO);
                }
                getContextList().get(i).setDataStatus(DataStatus.IMPORT_SUCCESS);
            } catch (Exception e) {
                error = true;
                log.error("参考数据导入出错", e);
                addErrorMsg(i, "参考数据导入出错:" + e.getMessage());
                getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
            }
        }
        return !error;
    }
}
