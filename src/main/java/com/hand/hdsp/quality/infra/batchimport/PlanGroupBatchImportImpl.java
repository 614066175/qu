package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 14:50
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN,sheetIndex = 1)
public class PlanGroupBatchImportImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;

    private final PlanGroupRepository planGroupRepository;

    public PlanGroupBatchImportImpl(ObjectMapper objectMapper, PlanGroupRepository planGroupRepository) {
        this.objectMapper = objectMapper;
        this.planGroupRepository = planGroupRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<PlanGroup> planGroups = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                PlanGroup planGroup = objectMapper.readValue(json, PlanGroup.class);
                planGroup.setTenantId(tenantId);
                planGroups.add(planGroup);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        planGroupRepository.batchInsertSelective(planGroups);
        return true;
    }
}
