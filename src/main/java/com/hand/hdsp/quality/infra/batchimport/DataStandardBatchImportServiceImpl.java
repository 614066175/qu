package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/04 15:26
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_STANDARD,sheetIndex = 1)
public class DataStandardBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final DataStandardRepository dataStandardRepository;
    private final StandardGroupRepository standardGroupRepository;

    public DataStandardBatchImportServiceImpl(ObjectMapper objectMapper, DataStandardRepository dataStandardRepository,
                                              StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<DataStandardDTO> dataStandardDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                DataStandardDTO dataStandardDTO = objectMapper.readValue(json, DataStandardDTO.class);
                //导入分组id
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataStandardDTO.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID,tenantId)
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID,projectId)
                ).build());
                if(CollectionUtils.isNotEmpty(standardGroupDTOList)){
                    dataStandardDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
                    dataStandardDTO.setGroupCode(standardGroupDTOList.get(0).getGroupCode());
                }
                dataStandardDTO.setTenantId(tenantId);
                dataStandardDTO.setProjectId(projectId);
                dataStandardDTOList.add(dataStandardDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        return dataStandardRepository.batchImport(dataStandardDTOList);
    }
}
