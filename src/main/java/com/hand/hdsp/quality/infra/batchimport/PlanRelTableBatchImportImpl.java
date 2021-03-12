package com.hand.hdsp.quality.infra.batchimport;

import java.util.List;

import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/11 15:14
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN,sheetIndex = 6)
public class PlanRelTableBatchImportImpl implements IBatchImportService {

    @Override
    public Boolean doImport(List<String> data) {
        return null;
    }
}
