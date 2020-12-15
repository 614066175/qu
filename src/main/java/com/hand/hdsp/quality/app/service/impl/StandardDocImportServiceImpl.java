package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * @author StoneHell
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC)
public class StandardDocImportServiceImpl implements IDoImportService {

    private final ObjectMapper objectMapper;
    private final StandardGroupRepository standardGroupRepository;
    private final StandardDocRepository standardDocRepository;

    public StandardDocImportServiceImpl(ObjectMapper objectMapper, StandardGroupRepository standardGroupRepository, StandardDocRepository standardDocRepository) {
        this.objectMapper = objectMapper;
        this.standardGroupRepository = standardGroupRepository;
        this.standardDocRepository = standardDocRepository;
    }

    @Override
    public Boolean doImport(String data) {
        StandardDocDTO standardDocDTO;
        try {
            standardDocDTO = objectMapper.readValue(data, StandardDocDTO.class);
        } catch (IOException e) {
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            // 失败
            return false;
        }
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        standardDocDTO.setTenantId(tenantId);
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, standardDocDTO.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, standardDocDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            standardDocDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
        } else {
            //创建分组
            StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                    .groupCode(standardDocDTO.getGroupCode())
                    .groupName(standardDocDTO.getGroupName())
                    .groupDesc(standardDocDTO.getGroupDesc())
                    .standardType(StandardConstant.StandardType.DOC)
                    .tenantId(standardDocDTO.getTenantId())
                    .build();
            standardGroupRepository.insertDTOSelective(standardGroupDTO);
            standardDocDTO.setGroupId(standardGroupDTO.getGroupId());
        }
        // 插入数据
        standardDocRepository.insertDTOSelective(standardDocDTO);
        return true;
    }
}
