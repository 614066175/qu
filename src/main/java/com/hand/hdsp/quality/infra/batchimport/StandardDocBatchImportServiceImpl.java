package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.converter.StandardDocConverter;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
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

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.DOC_STANDARD;

/**
 * @author StoneHell
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC, sheetIndex = 1)
public class StandardDocBatchImportServiceImpl extends BatchImportHandler implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;
    private final StandardGroupRepository standardGroupRepository;
    private final StandardDocMapper standardDocMapper;

    @Autowired
    private CommonGroupRepository commonGroupRepository;
    @Autowired
    private StandardDocConverter standardDocConverter;

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
        List<StandardDocDTO> addList = new ArrayList<>(data.size());
        List<StandardDoc> updateList = new ArrayList<>();
        try {
            for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
                String json = data.get(i);
                StandardDocDTO standardDocDTO = objectMapper.readValue(json, StandardDocDTO.class);
                //导入分组id
                CommonGroup commonGroup = commonGroupRepository.selectOne(CommonGroup.builder()
                        .groupType(DOC_STANDARD)
                        .groupPath(standardDocDTO.getGroupPath())
                        .tenantId(tenantId).projectId(projectId)
                        .build());
                if (commonGroup == null) {
                    addErrorMsg(i, "分组不存在，请先维护分组!");
                    continue;
                } else {
                    standardDocDTO.setGroupId(commonGroup.getGroupId());
                }
                standardDocDTO.setTenantId(tenantId);
                standardDocDTO.setProjectId(projectId);
                //设置责任人
                if (DataSecurityHelper.isTenantOpen()) {
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(standardDocDTO.getChargeName());
                    standardDocDTO.setChargeName(chargeName);
                }
                Long chargeId = standardDocMapper.checkCharger(standardDocDTO.getChargeName(), standardDocDTO.getTenantId());
                standardDocDTO.setChargeId(chargeId);

                StandardDoc exist = standardDocRepository.selectOne(StandardDoc.builder()
                        .standardCode(standardDocDTO.getStandardCode())
                        .tenantId(standardDocDTO.getTenantId())
                        .projectId(standardDocDTO.getProjectId())
                        .build());

                if (exist != null) {
                    standardDocDTO.setDocId(exist.getDocId());
                    standardDocDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    standardDocDTO.setDocPath(exist.getDocPath());
                    standardDocDTO.setDocName(exist.getDocName());
                    updateList.add(standardDocConverter.dtoToEntity(standardDocDTO));
                } else {
                    addList.add(standardDocDTO);
                }

                if (CollectionUtils.isNotEmpty(addList)) {
                    standardDocRepository.batchInsertDTOSelective(addList);
                }
                if (CollectionUtils.isNotEmpty(updateList)) {
                    standardDocRepository.batchUpdateByPrimaryKey(updateList);
                }
            }
        } catch (IOException e) {
            // 失败
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            return false;
        }
        return true;
    }
}
