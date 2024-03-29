package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.NameStandardDTO;
import org.xdsp.quality.domain.repository.NameStandardRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.mapper.NameStandardMapper;

import java.io.IOException;
import java.util.List;

/**
 * description
 * 命名标准校验逻辑
 * @author 29713 2021/08/26 14:13
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD, sheetIndex = 1)})
public class NameStandardValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final NameStandardRepository nameStandardRepository;
    private final NameStandardMapper nameStandardMapper;

    public NameStandardValidator(ObjectMapper objectMapper,
                                 NameStandardRepository nameStandardRepository,
                                 NameStandardMapper nameStandardMapper) {
        this.objectMapper = objectMapper;
        this.nameStandardRepository = nameStandardRepository;
        this.nameStandardMapper = nameStandardMapper;
    }

    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        for (int i = 0; i < data.size(); i++) {
            try {
                NameStandardDTO nameStandardDTO = objectMapper.readValue(data.get(i), NameStandardDTO.class);
                nameStandardDTO.setTenantId(tenantId);
                String standardCode = nameStandardDTO.getStandardCode();
                if(StringUtils.isEmpty(standardCode)){
                    addErrorMsg(i,"导入表格中字段命名标准不存在");
                    return false;
                }

                //如果有责任人，则进行验证
                //校验的责任人名称为员工姓名
                if(DataSecurityHelper.isTenantOpen()){
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(nameStandardDTO.getChargeName());
                    nameStandardDTO.setChargeName(chargeName);
                }
                List<Long> chargeIds = nameStandardMapper.checkCharger(nameStandardDTO.getChargeName(), nameStandardDTO.getTenantId());
                if (CollectionUtils.isEmpty(chargeIds)) {
                    addErrorMsg(i, "未找到此责任人，请检查数据");
                    return false;
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
