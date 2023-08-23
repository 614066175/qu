package org.xdsp.quality.infra.batchimport;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.StandardGroup;
import org.xdsp.quality.domain.repository.StandardGroupRepository;

import java.io.IOException;
import java.util.List;

import static org.xdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;

@Slf4j
//@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD, sheetIndex = 0)
@Deprecated
public class DataFieldStandardGroupBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final StandardGroupRepository standardGroupRepository;

    public DataFieldStandardGroupBatchImportServiceImpl(ObjectMapper objectMapper,
                                                        StandardGroupRepository standardGroupRepository) {
        this.objectMapper = objectMapper;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        //循环导入
        for (String json : data) {
            try {
                StandardGroupDTO standardGroupDTO = objectMapper.readValue(json, StandardGroupDTO.class);
                //根据分组Code在目标环境是否存在，若存在则更新
                List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID,tenantId)
                                .andEqualTo(StandardGroup.FIELD_PROJECT_ID,projectId)
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE,standardGroupDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,FIELD))
                        .build());
                if(ObjectUtils.isNotEmpty(standardGroupDTOS)){
                    //存在，进行更新
                    standardGroupDTO.setGroupId(standardGroupDTOS.get(0).getGroupId());
                    //查询并设置父分组id
                    if(StringUtils.isNotEmpty(standardGroupDTO.getParentGroupCode())){
                        List<StandardGroupDTO> parentStandardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                                        .andEqualTo(StandardGroup.FIELD_TENANT_ID,tenantId)
                                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID,projectId)
                                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE,standardGroupDTO.getParentGroupCode())
                                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,FIELD))
                                .build());
                        if(CollectionUtils.isNotEmpty(parentStandardGroupDTOList)){
                            standardGroupDTO.setParentGroupId(parentStandardGroupDTOList.get(0).getGroupId());
                        }
                    }
                    standardGroupDTO.setObjectVersionNumber(standardGroupDTOS.get(0).getObjectVersionNumber());
                    standardGroupRepository.updateByDTOPrimaryKeySelective(standardGroupDTO);
                }else {
                    //不存在，进行新增
                    standardGroupDTO.setTenantId(tenantId);
                    standardGroupDTO.setProjectId(projectId);
                    standardGroupDTO.setStandardType(FIELD);
                    //查询并设置父分组id
                    if(StringUtils.isNotEmpty(standardGroupDTO.getParentGroupCode())){
                        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId)
                                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID, projectId)
                                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, standardGroupDTO.getParentGroupCode())
                                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,FIELD))
                                .build());
                        if(CollectionUtils.isNotEmpty(standardGroupDTOList)){
                            StandardGroupDTO parentStandardGroupDTO = standardGroupDTOList.get(0);
                            standardGroupDTO.setParentGroupId(parentStandardGroupDTO.getGroupId());
                        }
                    }
                    standardGroupRepository.insertDTOSelective(standardGroupDTO);
                }
            } catch (IOException e) {
                log.error("导入分组失败");
                return false;
            }
        }
        return true;
    }
}
