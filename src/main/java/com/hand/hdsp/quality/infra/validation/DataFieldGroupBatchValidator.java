package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.List;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;

@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD, sheetIndex = 0)})
public class DataFieldGroupBatchValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final StandardGroupRepository standardGroupRepository;

    public DataFieldGroupBatchValidator(ObjectMapper objectMapper,
                                        StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public boolean validate(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        //校验
        try {
            for (int i = 0; i < data.size(); i++) {
                StandardGroupDTO standardGroupDTO = objectMapper.readValue(data.get(i), StandardGroupDTO.class);
                //校验分组
                if (StringUtils.isEmpty(standardGroupDTO.getGroupName())) {
                    addErrorMsg(i, "未导入分组名称");
                    return false;
                }
                //校验父分组
                String importParentGroupName = standardGroupDTO.getParentGroupName();
                if (StringUtils.isNotEmpty(importParentGroupName)) {
                    //查询分组
                    List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                                    .andEqualTo(StandardGroup.FIELD_GROUP_NAME, standardGroupDTO.getGroupName())
                                    .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,FIELD)
                                    .andEqualTo(StandardGroup.FIELD_PROJECT_ID, projectId)
                                    .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId)
                            )
                            .build());
                    if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
                        String groupName = standardGroupRepository.selectDTOByPrimaryKey(standardGroupDTOList.get(0).getGroupId()).getGroupName();
                        //当分组的父分组在本次导入表格中存在时 在导入环境不存在
                        if (!groupName.equals(importParentGroupName)) {
                            addErrorMsg(i, String.format("导入环境中父分组%s不存在", importParentGroupName));
                            return false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            return false;
        }
        return true;
    }
}
