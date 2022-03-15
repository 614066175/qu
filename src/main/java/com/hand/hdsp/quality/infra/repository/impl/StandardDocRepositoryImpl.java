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
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
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

    public StandardDocRepositoryImpl(StandardGroupRepository standardGroupRepository, StandardDocMapper standardDocMapper) {
        this.standardGroupRepository = standardGroupRepository;
        this.standardDocMapper = standardDocMapper;
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
                // 查找负责人id
                List<Long> chargeId = standardDocMapper.selectIdByChargeName(standardDocDTO.getChargeName(), standardDocDTO.getTenantId());
                if (CollectionUtils.isEmpty(chargeId)){
                    throw new CommonException("责任人不存在");
                }
                standardDocDTO.setChargeId(chargeId.get(0));
                // 查找负责部门id
                if (Strings.isNotEmpty(standardDocDTO.getChargeDeptName())) {
                    String chargeDeptName = standardDocDTO.getChargeDeptName();
                    // 判断是否加密
                    if (DataSecurityHelper.isTenantOpen()) {
                        chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
                    }
                    List<Long> chargeDeptId = standardDocMapper.selectIdByChargeDeptName(chargeDeptName, standardDocDTO.getTenantId());
                    if (CollectionUtils.isEmpty(chargeDeptId)) {
                        throw new CommonException("责任部门不存在");
                    }
                    standardDocDTO.setChargeDeptId(chargeDeptId.get(0));
                }
                List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_CODE, standardDocDTO.getGroupCode())
                                .andEqualTo(StandardGroup.FIELD_TENANT_ID, standardDocDTO.getTenantId())
                                .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, DOC))
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
            }
        }
        batchInsertDTOSelective(importStandardDocDTOList);
    }
}
