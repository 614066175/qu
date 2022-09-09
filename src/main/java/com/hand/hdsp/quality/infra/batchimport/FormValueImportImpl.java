package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.BaseFormValueDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.domain.entity.BaseFormValue;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BaseFormValueRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.core.oauth.DetailsHelper;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 15:14
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 6)
public class FormValueImportImpl extends BatchImportHandler implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final BaseFormValueRepository baseFormValueRepository;

    public FormValueImportImpl(ObjectMapper objectMapper, BatchPlanBaseRepository batchPlanBaseRepository, BaseFormValueRepository baseFormValueRepository) {
        this.objectMapper = objectMapper;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.baseFormValueRepository = baseFormValueRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        if (CollectionUtils.isEmpty(data)) {
            return true;
        }
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<BaseFormValueDTO> baseFormValueDTOList = new ArrayList<>(data.size());
        try {
            int index = 0;
            for (String json : data) {
                BaseFormValueDTO baseFormValueDTO = objectMapper.readValue(json, BaseFormValueDTO.class);
                baseFormValueDTO.setTenantId(tenantId);
                baseFormValueDTO.setProjectId(projectId);
                //查询planBaseId
                List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, baseFormValueDTO.getPlanBaseCode())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, baseFormValueDTO.getTenantId())
                                .andEqualTo(BatchPlanBase.FIELD_PROJECT_ID, baseFormValueDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(batchPlanBaseDTOS)) {
                    baseFormValueDTO.setPlanBaseId(batchPlanBaseDTOS.get(0).getPlanBaseId());
                } else {
                    addErrorMsg(index, String.format("质检项编码【%s】不存在", baseFormValueDTO.getPlanBaseCode()));
                    return false;
                }
                baseFormValueDTOList.add(baseFormValueDTO);
                index++;
            }
            if (!baseFormValueDTOList.isEmpty()) {
                //查询当前环境动态表单信息
                List<BaseFormValueDTO> itemList = baseFormValueRepository.selectFormItem();
                if (CollectionUtils.isNotEmpty(itemList)) {
                    Map<String, BaseFormValueDTO> formItemMap = itemList.stream().collect(Collectors.toMap(BaseFormValueDTO::getItemCode, Function.identity()));
                    baseFormValueDTOList.removeIf(baseFormValueDTO -> {
                        if (formItemMap.containsKey(baseFormValueDTO.getItemCode())) {
                            BaseFormValueDTO base = formItemMap.get(baseFormValueDTO.getItemCode());
                            baseFormValueDTO.setFormLineId(base.getFormLineId());
                            List<BaseFormValueDTO> baseDTOList = baseFormValueRepository.selectDTOByCondition(Condition.builder(BaseFormValue.class)
                                    .where(Sqls.custom()
                                            .andEqualTo(BaseFormValue.FIELD_TENANT_ID, tenantId)
                                            .andEqualTo(BaseFormValue.FIELD_PROJECT_ID, projectId)
                                            .andEqualTo(BaseFormValue.FIELD_PLAN_BASE_ID, baseFormValueDTO.getPlanBaseId())
                                            .andEqualTo(BaseFormValue.FIELD_FORM_LINE_ID, baseFormValueDTO.getFormLineId())
                                    )
                                    .build());
                            if(CollectionUtils.isNotEmpty(baseDTOList)){
                                BaseFormValueDTO old = baseDTOList.get(0);
                                old.setFormValue(baseFormValueDTO.getFormValue());
                                baseFormValueRepository.updateDTOOptional(old,BaseFormValue.FIELD_FORM_VALUE);
                                return true;
                            }
                            return false;
                        }
                        return true;
                    });
                }
                // 插入扩展字段值
                baseFormValueRepository.batchInsertDTOSelective(baseFormValueDTOList);
            }

            return true;
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
    }
}
