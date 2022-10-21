package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DOC;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.CREATE;

/**
 * @author StoneHell
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC,sheetIndex = 1)
public class StandardDocBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;
    private final StandardGroupRepository standardGroupRepository;
    private final StandardDocMapper standardDocMapper;

    public StandardDocBatchImportServiceImpl(ObjectMapper objectMapper,
                                             StandardDocRepository standardDocRepository,
                                             StandardGroupRepository standardGroupRepository,
                                             StandardDocMapper standardDocMapper) {
        this.objectMapper = objectMapper;
        this.standardDocRepository = standardDocRepository;
        this.standardGroupRepository = standardGroupRepository;
        this.standardDocMapper = standardDocMapper;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<StandardDocDTO> standardDocDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                StandardDocDTO standardDocDTO = objectMapper.readValue(json, StandardDocDTO.class);
                //导入分组id
                String groupName = standardDocDTO.getGroupName();
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_NAME, groupName)
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, DOC)
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID,tenantId)
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID,projectId)
                ).build());
                if(CollectionUtils.isNotEmpty(standardGroupDTOList)){
                    standardDocDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
                }
                standardDocDTO.setTenantId(tenantId);
                standardDocDTO.setProjectId(projectId);
                //设置责任人
                if(DataSecurityHelper.isTenantOpen()){
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(standardDocDTO.getChargeName());
                    standardDocDTO.setChargeName(chargeName);
                }
                Long chargeId = standardDocMapper.checkCharger(standardDocDTO.getChargeName(), standardDocDTO.getTenantId());
                standardDocDTO.setChargeId(chargeId);
                standardDocDTOList.add(standardDocDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            return false;
        }
        standardDocRepository.batchInsertDTOSelective(standardDocDTOList);
        return true;
    }
}
