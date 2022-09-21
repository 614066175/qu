package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

import java.io.IOException;
import java.util.List;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;

/**
 * 数据标准 分组导入
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_STANDARD,sheetIndex = 0)
public class DataStandardGroupBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final StandardGroupRepository standardGroupRepository;

    public DataStandardGroupBatchImportServiceImpl(ObjectMapper objectMapper,
                                                   StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        //循环导入
        for (String json:data){
            try {
                StandardGroupDTO standardGroupDTO = objectMapper.readValue(json, StandardGroupDTO.class);
                //因为校验过了就直接导入
                standardGroupDTO.setTenantId(tenantId);
                standardGroupDTO.setProjectId(projectId);
                standardGroupDTO.setStandardType(DATA);
                standardGroupRepository.insertDTOSelective(standardGroupDTO);
            } catch (IOException e) {
                log.error("导入分组失败");
                return false;
            }
        }
        return true;
    }
}
