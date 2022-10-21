package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.*;

@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD,sheetIndex = 1)
public class NameStandardBatchImportImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final NameStandardRepository nameStandardRepository;
    private final StandardGroupRepository standardGroupRepository;
    private final NameStandardMapper nameStandardMapper;

    public NameStandardBatchImportImpl(ObjectMapper objectMapper,
                                       NameStandardRepository nameStandardRepository,
                                       StandardGroupRepository standardGroupRepository,
                                       NameStandardMapper nameStandardMapper) {
        this.objectMapper = objectMapper;
        this.nameStandardRepository = nameStandardRepository;
        this.standardGroupRepository = standardGroupRepository;
        this.nameStandardMapper = nameStandardMapper;
    }

    @Override
    public Boolean doImport(List<String> data) {
        List<NameStandardDTO> nameStandardDTOList = new ArrayList<>(data.size());
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try{
            for (String json:data){
                NameStandardDTO nameStandardDTO = objectMapper.readValue(json,NameStandardDTO.class);
                //导入分组id
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, nameStandardDTO.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, NAME)
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID,tenantId)
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID,projectId)
                ).build());
                if(CollectionUtils.isNotEmpty(standardGroupDTOList)){
                    nameStandardDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
                }
                nameStandardDTO.setTenantId(tenantId);
                nameStandardDTO.setProjectId(projectId);
                //设置责任人
                if(DataSecurityHelper.isTenantOpen()){
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(nameStandardDTO.getChargeName());
                    nameStandardDTO.setChargeName(chargeName);
                }
                Long chargeId = nameStandardMapper.checkCharger(nameStandardDTO.getChargeName(), nameStandardDTO.getTenantId());
                nameStandardDTO.setChargeId(chargeId);
                nameStandardDTOList.add(nameStandardDTO);
            }
        }catch (IOException e){
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        nameStandardRepository.batchInsertDTOSelective(nameStandardDTOList);
        return true;
    }
}
