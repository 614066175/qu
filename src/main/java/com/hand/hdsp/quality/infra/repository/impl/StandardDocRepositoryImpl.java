package com.hand.hdsp.quality.infra.repository.impl;

import java.util.LinkedList;
import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>标准文档管理表资源库实现</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Component
public class StandardDocRepositoryImpl extends BaseRepositoryImpl<StandardDoc, StandardDocDTO> implements StandardDocRepository {

    private final StandardGroupRepository standardGroupRepository;

    public StandardDocRepositoryImpl(StandardGroupRepository standardGroupRepository) {
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public void batchImport(List<StandardDocDTO> standardDocDTOList) {
        List<StandardDocDTO> importStandardDocDTOList = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
            standardDocDTOList.forEach(standardDocDTO -> {
                List<StandardDoc> standardDocs;
                standardDocs = selectByCondition(Condition.builder(StandardDoc.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardDoc.FIELD_STANDARD_CODE, standardDocDTO.getStandardCode())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        ).build());
                if (CollectionUtils.isNotEmpty(standardDocs)) {
                    return;
                }
                standardDocs = selectByCondition(Condition.builder(StandardDoc.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        ).build());
                if (CollectionUtils.isNotEmpty(standardDocs)) {
                    return;
                }
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
                importStandardDocDTOList.add(standardDocDTO);
            });
        }
        batchInsertDTOSelective(importStandardDocDTOList);
    }
}
