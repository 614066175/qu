package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;

/**
 * @author wsl
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD,sheetIndex = 1)
public class DataFieldStandardBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final DataFieldRepository dataFieldRepository;
    private final StandardGroupRepository standardGroupRepository;

    public DataFieldStandardBatchImportServiceImpl(ObjectMapper objectMapper
            , DataFieldRepository dataFieldRepository
            , StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<DataFieldDTO> dataFieldDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                DataFieldDTO dataFieldDTO = objectMapper.readValue(json, DataFieldDTO.class);
                //导入分组id
                String groupName = dataFieldDTO.getGroupName();
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_NAME, groupName)
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID,tenantId)
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID,projectId)
                ).build());
                if(CollectionUtils.isNotEmpty(standardGroupDTOList)){
                    dataFieldDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
                }
                dataFieldDTO.setTenantId(tenantId);
                dataFieldDTO.setProjectId(projectId);
                dataFieldDTOList.add(dataFieldDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            return false;
        }
        return dataFieldRepository.batchImport(dataFieldDTOList);
    }
}
