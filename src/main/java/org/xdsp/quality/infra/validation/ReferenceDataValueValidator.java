package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.ReferenceDataValueDTO;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.entity.ReferenceDataValue;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.domain.repository.ReferenceDataValueRepository;
import org.xdsp.quality.infra.constant.ReferenceDataConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.util.List;
import java.util.Objects;

/**
 * 参考数据参考值校验
 * @author fuqiang.luo@hand-china.com
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_REFERENCE_DATA, sheetIndex = 2)})
public class ReferenceDataValueValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final ReferenceDataRepository referenceDataRepository;
    private final ReferenceDataValueRepository referenceDataValueRepository;

    public ReferenceDataValueValidator(ObjectMapper objectMapper,
                                       ReferenceDataRepository referenceDataRepository,
                                       ReferenceDataValueRepository referenceDataValueRepository) {
        this.objectMapper = objectMapper;
        this.referenceDataRepository = referenceDataRepository;
        this.referenceDataValueRepository = referenceDataValueRepository;
    }


    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        for (int i = 0; i < data.size(); i++) {
            try {
                ReferenceDataValueDTO referenceDataValueDTO = objectMapper.readValue(data.get(i), ReferenceDataValueDTO.class);
                String dataCode = referenceDataValueDTO.getDataCode();
                if (StringUtils.isBlank(dataCode)) {
                    log.error("参考数据不能为空");
                    addErrorMsg(i, "参考数据不能为空");
                    return false;
                }
                String value = referenceDataValueDTO.getValue();
                if (StringUtils.isBlank(value)) {
                    log.error("参考数据值不能为空");
                    addErrorMsg(i, "参考数据值不能为空");
                    return false;
                }
                String parentValue = referenceDataValueDTO.getParentValue();
                List<ReferenceData> referenceDataList = referenceDataRepository.select(ReferenceData
                        .builder()
                        .dataCode(dataCode)
                        .projectId(projectId)
                        .tenantId(tenantId)
                        .build());
                if (CollectionUtils.isNotEmpty(referenceDataList)) {
                    // 如果参考数据头表记录已经存在 这里就校验一下参考值是否重复 否则不校验 留到导入时校验
                    ReferenceData referenceData = referenceDataList.get(0);
                    if (ReferenceDataConstant.RELEASED.equals(referenceData.getDataStatus())) {
                        // 在线 不允许进行后续修改
                        log.error("参考数据(编码[{}])已经存在且已上线", referenceData.getDataCode());
                        addErrorMsg(i, "参考数据已经存在且已上线");
                        return false;
                    }
                    Long parentDataId = referenceData.getParentDataId();
                    if (Objects.isNull(parentDataId) && StringUtils.isNotBlank(parentValue)) {
                        log.error("参考数据(编码[{}])没有父参考数据，不能维护父参考值", referenceData.getDataCode());
                        addErrorMsg(i, "参考数据没有父参考数据，不能维护父参考值");
                        return false;
                    }
                    if (StringUtils.isNotBlank(parentValue)) {
                        int parentCount = referenceDataValueRepository.selectCount(ReferenceDataValue
                                .builder()
                                .value(parentValue)
                                .dataId(parentDataId)
                                .projectId(projectId)
                                .tenantId(tenantId)
                                .build());
                        if (parentCount <= 0) {
                            log.error("父参考值[{}]不存在", parentValue);
                            addErrorMsg(i, "父参考值不存在");
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("参考数据值校验出错", e);
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
