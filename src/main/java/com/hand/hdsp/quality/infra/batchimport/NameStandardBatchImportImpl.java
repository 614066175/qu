package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.converter.NameStandardConverter;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.NAME_STANDARD;

@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD, sheetIndex = 1)
public class NameStandardBatchImportImpl extends BatchImportHandler implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final NameStandardRepository nameStandardRepository;
    private final StandardGroupRepository standardGroupRepository;
    private final NameStandardMapper nameStandardMapper;

    @Autowired
    private CommonGroupRepository commonGroupRepository;
    @Autowired
    private NameStandardConverter nameStandardConverter;


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
        List<NameStandardDTO> addList = new ArrayList<>();
        List<NameStandard> updateList = new ArrayList<>();
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
                String json = data.get(i);
                NameStandardDTO nameStandardDTO = objectMapper.readValue(json, NameStandardDTO.class);
                //导入分组id
                CommonGroup commonGroup = commonGroupRepository.selectOne(CommonGroup.builder()
                        .groupType(NAME_STANDARD)
                        .groupPath(nameStandardDTO.getGroupPath())
                        .tenantId(tenantId).projectId(projectId).build());
                if (commonGroup == null) {
                    addErrorMsg(i, "分组不存在，请先维护分组!");
                    continue;
                } else {
                    nameStandardDTO.setGroupId(commonGroup.getGroupId());
                }
                nameStandardDTO.setTenantId(tenantId);
                nameStandardDTO.setProjectId(projectId);
                //设置责任人
                if (DataSecurityHelper.isTenantOpen()) {
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(nameStandardDTO.getChargeName());
                    nameStandardDTO.setChargeName(chargeName);
                }
                Long chargeId = nameStandardMapper.checkCharger(nameStandardDTO.getChargeName(), nameStandardDTO.getTenantId());
                nameStandardDTO.setChargeId(chargeId);


                //判断命名标准是否存在
                NameStandard exist = nameStandardRepository.selectOne(NameStandard.builder()
                        .standardCode(nameStandardDTO.getStandardCode())
                        .tenantId(nameStandardDTO.getTenantId())
                        .projectId(nameStandardDTO.getProjectId())
                        .build());
                if (exist != null) {
                    nameStandardDTO.setStandardId(exist.getStandardId());
                    nameStandardDTO.setLatestCheckedStatus(exist.getLatestCheckedStatus());
                    nameStandardDTO.setLatestAbnormalNum(exist.getLatestAbnormalNum());
                    nameStandardDTO.setEnabledFlag(exist.getEnabledFlag());
                    nameStandardDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    updateList.add(nameStandardConverter.dtoToEntity(nameStandardDTO));
                } else {
                    addList.add(nameStandardDTO);
                }
            }

            if (CollectionUtils.isNotEmpty(addList)) {
                nameStandardRepository.batchInsertDTOSelective(addList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                nameStandardRepository.batchUpdateByPrimaryKey(updateList);
            }
        } catch (IOException e) {
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }

        return true;
    }
}
