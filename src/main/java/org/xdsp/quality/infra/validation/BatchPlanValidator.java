package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.xdsp.quality.api.dto.BatchPlanDTO;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.util.ValidatorUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @Title: BatchPlanValidator
 * @Description:
 * @author: lgl
 * @date: 2022/10/23 19:14
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 1)})
public class BatchPlanValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final PlanGroupRepository planGroupRepository;

    public BatchPlanValidator(ObjectMapper objectMapper, PlanGroupRepository planGroupRepository) {
        this.objectMapper = objectMapper;
        this.planGroupRepository = planGroupRepository;
    }

    @Override
    public boolean validate(List<String> data) {
        //直接从线程变量中拿
        ThreadLocal<Set<String>> groupCodeThreadLocal = ValidatorUtil.getGroupCodeThreadLocal();
        Set<String> groupCodes = groupCodeThreadLocal.get();
        try {
            for (int i = 0; i < data.size(); i++) {
                BatchPlanDTO batchPlanDTO = objectMapper.readValue(data.get(i), BatchPlanDTO.class);
                //校验方案分组编码是否存在
                //没有分组编码，或者编码集合不包含此分组的父编码
                if (CollectionUtils.isEmpty(groupCodes) || !groupCodes.contains(batchPlanDTO.getGroupCode())) {
                    addErrorMsg(i, batchPlanDTO.getGroupCode() + "分组不存在，请先维护分组信息");
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;

    }
}
