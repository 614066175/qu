package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.util.ValidatorUtil;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/15 14:47
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 0)})
public class PlanGroupValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;
    private final PlanGroupRepository planGroupRepository;

    public PlanGroupValidator(ObjectMapper objectMapper, PlanGroupRepository planGroupRepository) {
        this.objectMapper = objectMapper;
        this.planGroupRepository = planGroupRepository;
    }

    @Override
    public boolean validate(List<String> data) {
        ThreadLocal<Set<String>> groupCodeThreadLocal = ValidatorUtil.getGroupCodeThreadLocal();
        Set<String> groupCodes = new HashSet<>();
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<PlanGroup> planGroups = planGroupRepository.select(PlanGroup.builder()
                .tenantId(tenantId).projectId(ProjectHelper.getProjectId())
                .build());
        //查询获取分组编码数据
        if (CollectionUtils.isNotEmpty(planGroups)) {
            groupCodes.addAll(planGroups.stream().map(PlanGroup::getGroupCode).collect(Collectors.toSet()));
        }
        try {
            for (int i = 0; i < data.size(); i++) {
                PlanGroupDTO planGroupDTO = objectMapper.readValue(data.get(i), PlanGroupDTO.class);
                groupCodes.add(planGroupDTO.getGroupCode());
                //校验父分组编码是否存在
                if (StringUtils.isNotEmpty(planGroupDTO.getParentGroupCode())) {
                    //没有分组编码，或者编码集合不包含此分组的父编码
                    if (CollectionUtils.isEmpty(groupCodes) || !groupCodes.contains(planGroupDTO.getParentGroupCode())) {
                        addErrorMsg(i, planGroupDTO.getParentGroupCode() + "父分组不存在");
                    }
                }
            }
            //将分组编码信息设置到线程变量中，供方案检验使用
            groupCodeThreadLocal.set(groupCodes);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
