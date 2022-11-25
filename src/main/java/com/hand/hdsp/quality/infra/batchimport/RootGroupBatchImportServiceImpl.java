package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.ROOT;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2022/11/22 20:40
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_ROOT,sheetIndex = 0)
public class RootGroupBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final StandardGroupRepository standardGroupRepository;

    public RootGroupBatchImportServiceImpl(ObjectMapper objectMapper, StandardGroupRepository standardGroupRepository) {
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
                                .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,ROOT))
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
                                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,ROOT))
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
                    standardGroupDTO.setStandardType(ROOT);
                    //查询并设置父分组id
                    if(StringUtils.isNotEmpty(standardGroupDTO.getParentGroupCode())){
                        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId)
                                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID, projectId)
                                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, standardGroupDTO.getParentGroupCode())
                                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE,ROOT))
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
