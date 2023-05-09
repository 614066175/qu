package com.hand.hdsp.quality.infra.validation;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRepository;
import com.hand.hdsp.quality.infra.constant.ReferenceDataConstant;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.ReferenceDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 *  参考数据头表校验
 * @author fuqiang.luo@hand-china.com
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_REFERENCE_DATA, sheetIndex = 1)})
public class ReferenceDataValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final ReferenceDataRepository referenceDataRepository;
    private final ReferenceDataMapper referenceDataMapper;

    public ReferenceDataValidator(ObjectMapper objectMapper,
                                  ReferenceDataRepository referenceDataRepository,
                                  ReferenceDataMapper referenceDataMapper) {
        this.objectMapper = objectMapper;
        this.referenceDataRepository = referenceDataRepository;
        this.referenceDataMapper = referenceDataMapper;
    }

    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        for (int i = 0; i < data.size(); i++) {
            try {
                ReferenceDataDTO referenceDataDTO = objectMapper.readValue(data.get(i), ReferenceDataDTO.class);
                // 分组全路径 分组不存在是会自动创建 不校验
                // 参考数据编码
                String dataCode = referenceDataDTO.getDataCode();
                if (StringUtils.isBlank(dataCode)) {
                    log.error("参考数据编码不能为空");
                    addErrorMsg(i, "参考数据编码不能为空");
                    return false;
                }
                List<ReferenceData> referenceDataList = referenceDataRepository.select(ReferenceData
                        .builder()
                        .dataCode(dataCode)
                        .projectId(projectId)
                        .tenantId(tenantId)
                        .build());
                if (CollectionUtils.isNotEmpty(referenceDataList)) {
                    // 参考数据头表已经存在 检查是否可以修改
                    ReferenceData exist = referenceDataList.get(0);
                    if (ReferenceDataConstant.RELEASED.equals(exist.getDataStatus())) {
                        // 在线 不允许进行后续修改
                        log.error("参考数据(编码[{}])已经存在且已上线", exist.getDataCode());
                        addErrorMsg(i, "参考数据已经存在且已上线");
                        return false;
                    }
                }

                // 参考数据名称
                String dataName = referenceDataDTO.getDataName();
                if (StringUtils.isBlank(dataName)) {
                    log.error("参考数据名称不能为空");
                    addErrorMsg(i, "参考数据名称不能为空");
                    return false;
                }
                int count = referenceDataRepository.selectCount(ReferenceData
                        .builder()
                        .dataName(dataName)
                        .projectId(projectId)
                        .tenantId(tenantId)
                        .build());
                if (count > 0) {
                    log.error("参考数据名称已经存在");
                    addErrorMsg(i, "参考数据名称已经存在");
                    return false;
                }

                // 父参考数据
                String parentDataCode = referenceDataDTO.getParentDataCode();
                if (StringUtils.isNotBlank(parentDataCode)) {
                    int parentCount  = referenceDataRepository.selectCount(ReferenceData
                            .builder()
                            .dataCode(parentDataCode)
                            .projectId(projectId)
                            .tenantId(tenantId)
                            .build());
                    if (parentCount <= 0) {
                        log.error("父参考数据(编码[{}])不存在", parentDataCode);
                        addErrorMsg(i, String.format("父参考数据(编码[%s])不存在", parentDataCode));
                        return false;
                    }
                }

                // 责任人校验
                String responsiblePersonName = referenceDataDTO.getResponsiblePersonName();
                if (StringUtils.isBlank(responsiblePersonName)) {
                    log.error("责任人不能为空");
                    addErrorMsg(i, "责任人不能为空");
                    return false;
                }
                // 查询责任人是否存在
                String employeeName = responsiblePersonName;
                if (DataSecurityHelper.isTenantOpen()) {
                    // 加密后查询
                    employeeName = DataSecurityHelper.encrypt(responsiblePersonName);
                }
                List<Long> employeeIds = referenceDataMapper.queryEmployeeIdsByName(employeeName, tenantId);
                if (CollectionUtils.isEmpty(employeeIds)) {
                    log.error("责任人错误！员工[{}]不存在", responsiblePersonName);
                    addErrorMsg(i, String.format("责任人错误！员工[%s]不存在", responsiblePersonName));
                    return false;
                }

                // 责任部门校验
                String responsibleDeptName = referenceDataDTO.getResponsibleDeptName();
                if (StringUtils.isNotBlank(responsibleDeptName)) {
                    // 检查责任部门是否存在
                    String deptName = responsibleDeptName;
                    if (DataSecurityHelper.isTenantOpen()) {
                        deptName = DataSecurityHelper.encrypt(responsibleDeptName);
                    }
                    List<Long> deptIds = referenceDataMapper.queryDeptIdsByName(deptName, tenantId);
                    if (CollectionUtils.isEmpty(deptIds)) {
                        log.error("责任部门错误！部门[{}]不存在", responsibleDeptName);
                        addErrorMsg(i, String.format("责任部门错误！部门[%s]不存在", responsibleDeptName));
                        return false;
                    }
                }
            } catch (Exception e) {
                log.error("参考数据校验出错", e);
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
