package com.hand.hdsp.quality.infra.repository.impl;

import java.util.LinkedList;
import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
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
                                .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardCode())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        ).build());
                if (CollectionUtils.isNotEmpty(standardDocs)) {
                    return;
                }
                importStandardDocDTOList.add(standardDocDTO);
            });
        }
        batchInsertDTOSelective(importStandardDocDTOList);
    }
}
