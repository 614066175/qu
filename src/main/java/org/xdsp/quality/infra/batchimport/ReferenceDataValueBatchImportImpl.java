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
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.ReferenceDataValueDTO;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.entity.ReferenceDataValue;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.domain.repository.ReferenceDataValueRepository;
import org.xdsp.quality.infra.constant.ReferenceDataConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.converter.ReferenceDataValueConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 参考值导入
 * @author fuqiang.luo@hand-china.com
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_REFERENCE_DATA, sheetIndex = 2)
public class ReferenceDataValueBatchImportImpl extends BatchImportHandler implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final ReferenceDataRepository referenceDataRepository;
    private final ReferenceDataValueRepository referenceDataValueRepository;
    private final ReferenceDataValueConverter referenceDataValueConverter;

    public ReferenceDataValueBatchImportImpl(ObjectMapper objectMapper,
                                             ReferenceDataRepository referenceDataRepository,
                                             ReferenceDataValueRepository referenceDataValueRepository,
                                             ReferenceDataValueConverter referenceDataValueConverter) {
        this.objectMapper = objectMapper;
        this.referenceDataRepository = referenceDataRepository;
        this.referenceDataValueRepository = referenceDataValueRepository;
        this.referenceDataValueConverter = referenceDataValueConverter;
    }

    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        boolean error = false;
        for (int i = 0; i < data.size(); i++) {
            try {
                ReferenceDataValueDTO referenceDataValueDTO = objectMapper.readValue(data.get(i), ReferenceDataValueDTO.class);
                referenceDataValueDTO.setEnabledFlag(1);
                referenceDataValueDTO.setProjectId(projectId);
                referenceDataValueDTO.setTenantId(tenantId);
                // 查询参考数据ID
                List<ReferenceData> referenceDataList = referenceDataRepository.select(ReferenceData
                        .builder()
                        .dataCode(referenceDataValueDTO.getDataCode())
                        .projectId(projectId)
                        .tenantId(tenantId)
                        .build());
                if (CollectionUtils.isEmpty(referenceDataList)) {
                    String dataCode = referenceDataValueDTO.getDataCode();
                    log.error("参考数据(编码[{}])不存在", dataCode);
                    addErrorMsg(i, String.format("参考数据(编码[%s])不存在", dataCode));
                    getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
                    continue;
                }
                ReferenceData referenceData = referenceDataList.get(0);
                Long dataId = referenceData.getDataId();
                String dataCode = referenceData.getDataCode();
                referenceDataValueDTO.setDataId(dataId);
                if (ReferenceDataConstant.RELEASED.equals(referenceData.getDataStatus())) {
                    // 在线 不允许进行后续修改
                    log.error("参考数据(编码[{}])已经存在且已上线", dataCode);
                    addErrorMsg(i, "参考数据已经存在且已上线");
                    getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
                    continue;
                }
                // 父参考值id
                String parentValue = referenceDataValueDTO.getParentValue();
                if (Objects.isNull(referenceData.getParentDataId()) && StringUtils.isNotBlank(parentValue)) {
                    log.error("参考数据(编码[{}])没有父参考数据，不能维护父参考值", referenceData.getDataCode());
                    addErrorMsg(i, "参考数据没有父参考数据，不能维护父参考值");
                    getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
                    continue;
                }
                if (StringUtils.isNotBlank(parentValue)) {
                    List<ReferenceDataValue> parentReferenceDataValueList = referenceDataValueRepository.select(ReferenceDataValue
                            .builder()
                            .value(parentValue)
                            .dataId(referenceData.getParentDataId())
                            .projectId(projectId)
                            .tenantId(tenantId)
                            .build());
                    if (CollectionUtils.isNotEmpty(parentReferenceDataValueList)) {
                        referenceDataValueDTO.setParentValueId(parentReferenceDataValueList.get(0).getValueId());
                    }
                }
                String value = referenceDataValueDTO.getValue();
                List<ReferenceDataValue> referenceDataValueList = referenceDataValueRepository.select(ReferenceDataValue
                        .builder()
                        .value(value)
                        .dataId(dataId)
                        .projectId(projectId)
                        .tenantId(tenantId)
                        .build());
                if (CollectionUtils.isNotEmpty(referenceDataValueList)) {
                    ReferenceDataValue exist = referenceDataValueList.get(0);
                    referenceDataValueDTO.setValueId(exist.getValueId());
                    referenceDataValueDTO.setEnabledFlag(exist.getEnabledFlag());
                    referenceDataValueDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    referenceDataValueRepository.updateByDTOPrimaryKey(referenceDataValueDTO);
                } else {
                    referenceDataValueRepository.insertDTOSelective(referenceDataValueDTO);
                }
                getContextList().get(i).setDataStatus(DataStatus.IMPORT_SUCCESS);
            } catch (Exception e) {
                error = true;
                log.error("参考值导入出错", e);
                addErrorMsg(i, "参考值导入出错:" + e.getMessage());
                getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
            }
        }
        return !error;
    }
}
