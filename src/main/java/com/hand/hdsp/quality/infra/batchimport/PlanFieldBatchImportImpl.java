package com.hand.hdsp.quality.infra.batchimport;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 15:13
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN,sheetIndex = 5)
public class PlanFieldBatchImportImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final BatchPlanFieldRepository batchPlanFieldRepository;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    public PlanFieldBatchImportImpl(ObjectMapper objectMapper, BatchPlanFieldRepository batchPlanFieldRepository, BatchPlanBaseRepository batchPlanBaseRepository) {
        this.objectMapper = objectMapper;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        return null;
    }
}
