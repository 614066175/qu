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
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import com.hand.hdsp.quality.infra.util.ImportUtil;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DOC;

/**
 * <p>标准文档管理表资源库实现</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Component
public class StandardDocRepositoryImpl extends BaseRepositoryImpl<StandardDoc, StandardDocDTO> implements StandardDocRepository {

    private final StandardGroupRepository standardGroupRepository;
    private final StandardDocMapper standardDocMapper;
    private final ImportUtil importUtil;

    public StandardDocRepositoryImpl(StandardGroupRepository standardGroupRepository, StandardDocMapper standardDocMapper, ImportUtil importUtil) {
        this.standardGroupRepository = standardGroupRepository;
        this.standardDocMapper = standardDocMapper;
        this.importUtil = importUtil;
    }

    @Override
    public void batchImport(List<StandardDocDTO> standardDocDTOList) {
        List<StandardDocDTO> importStandardDocDTOList = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(standardDocDTOList)) {
            // 判断标准文档是否存在
            for (StandardDocDTO standardDocDTO : standardDocDTOList) {
                List<StandardDoc> standardDocs;
                standardDocs = selectByCondition(Condition.builder(StandardDoc.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardDoc.FIELD_STANDARD_CODE, standardDocDTO.getStandardCode())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        ).build());
                if (CollectionUtils.isNotEmpty(standardDocs)) {
                    throw new CommonException("标准编码已存在");
                }
                standardDocs = selectByCondition(Condition.builder(StandardDoc.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardDoc.FIELD_STANDARD_NAME, standardDocDTO.getStandardName())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                        ).build());
                if (CollectionUtils.isNotEmpty(standardDocs)) {
                    throw new CommonException("标准名称已存在");
                }

                //根据责任人姓名 获取目标环境的责任人id
                standardDocDTO.setChargeDeptId(importUtil.getChargeDeptId(standardDocDTO.getChargeDeptName(), standardDocDTO.getTenantId()));
                standardDocDTO.setChargeId(importUtil.getChargerId(standardDocDTO.getChargeName(), standardDocDTO.getTenantId()));


                //创建分组
                StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                        .groupCode(standardDocDTO.getGroupCode())
                        .groupName(standardDocDTO.getGroupName())
                        .groupDesc(standardDocDTO.getGroupDesc())
                        .standardType(StandardConstant.StandardType.DOC)
                        .tenantId(standardDocDTO.getTenantId())
                        .build();

                //有则返回，无则新建
                StandardGroupDTO standardGroup = importUtil.getStandardGroup(standardGroupDTO);
                standardDocDTO.setGroupId(standardGroup.getGroupId());

                importStandardDocDTOList.add(standardDocDTO);
            }
        }
        batchInsertDTOSelective(importStandardDocDTOList);
    }
}
