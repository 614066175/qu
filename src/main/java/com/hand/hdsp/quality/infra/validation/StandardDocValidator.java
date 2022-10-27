package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;

import com.hand.hdsp.quality.domain.entity.StandardDoc;

import com.hand.hdsp.quality.domain.repository.StandardDocRepository;

import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;



/**
 * @author StoneHell
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC,sheetIndex = 1)})
public class StandardDocValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;
    private final StandardDocMapper standardDocMapper;

    public StandardDocValidator(ObjectMapper objectMapper,
                                StandardDocRepository standardDocRepository,
                                StandardDocMapper standardDocMapper) {
        this.objectMapper = objectMapper;
        this.standardDocRepository = standardDocRepository;
        this.standardDocMapper = standardDocMapper;
    }


    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (int i = 0; i < data.size(); i++) {
                if (StringUtils.isNoneBlank(data.get(i))) {
                    StandardDocDTO standardDocDTO = objectMapper.readValue(data.get(i), StandardDocDTO.class);
                    List<StandardDocDTO> standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(StandardDoc.FIELD_STANDARD_CODE, standardDocDTO.getStandardCode())
                                    .andEqualTo(StandardDoc.FIELD_TENANT_ID, tenantId)
                                    .andEqualTo(StandardDoc.FIELD_PROJECT_ID,projectId))
                            .build());
                    //标准编码存在
                    if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
                        addErrorMsg(i, "标准编码存已在");
                        return false;
                    }
                    standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName())
                                    .andEqualTo(StandardDoc.FIELD_TENANT_ID, tenantId)
                                    .andEqualTo(StandardDoc.FIELD_PROJECT_ID,projectId))
                            .build());
                    //标准名称存在
                    if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
                        addErrorMsg(i, "标准名称已存在");
                        return false;
                    }

                    //标准编码不符合规范
                    if (!standardDocDTO.getStandardCode().matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                        addErrorMsg(i, "标准编码不符合:1-64个字符，可包含字母、数字或下划线”_”，英文字母开头");
                        return false;
                    }
                    //如果有责任人，则进行验证
                    //校验的责任人名称为员工姓名
                    if(DataSecurityHelper.isTenantOpen()){
                        //加密后查询
                        String chargeName = DataSecurityHelper.encrypt(standardDocDTO.getChargeName());
                        standardDocDTO.setChargeName(chargeName);
                    }
                    Long chargeId = standardDocMapper.checkCharger(standardDocDTO.getChargeName(), tenantId);
                    if (ObjectUtils.isEmpty(chargeId)) {
                        addErrorMsg(i, "未找到此责任人，请检查数据");
                        return false;
                    }
                    //当sheet页”数据标准“中的分组名称在本次导入表格和系统中不存在时则不能导入，并提示”${分组}分组不存在“，并在对应单元格高亮警示
                    String groupCode = standardDocDTO.getGroupCode();
                    if (StringUtils.isEmpty(groupCode)) {
                        addErrorMsg(i, "表格中数据标准sheet页有标准未设置分组");
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
