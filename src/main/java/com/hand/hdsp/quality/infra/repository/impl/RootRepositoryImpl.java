package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.core.util.DataSecurityUtil;
import com.hand.hdsp.quality.api.dto.AssigneeUserDTO;
import com.hand.hdsp.quality.api.dto.RootDTO;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.entity.RootLine;
import com.hand.hdsp.quality.domain.repository.RootLineRepository;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.infra.mapper.RootMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;

/**
 * 词根 资源库实现
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@Component
public class RootRepositoryImpl extends BaseRepositoryImpl<Root, RootDTO> implements RootRepository {

    private final RootMapper rootMapper;
    private final RootLineRepository rootLineRepository;

    public RootRepositoryImpl(RootMapper rootMapper, RootLineRepository rootLineRepository) {
        this.rootMapper = rootMapper;
        this.rootLineRepository = rootLineRepository;
    }

    @Override
    public List<Root> list(Root root) {
        if(DataSecurityHelper.isTenantOpen()){
            root.setChargeName(DataSecurityUtil.encrypt(root.getChargeName()));
        }

        List<Root> rootList = rootMapper.list(root);
        if(DataSecurityHelper.isTenantOpen()){
            for(Root tmp:rootList){
                tmp.setChargeDept(DataSecurityUtil.decrypt(tmp.getChargeDept()));
                tmp.setChargeTel(DataSecurityUtil.decrypt(tmp.getChargeTel()));
                tmp.setChargeEmail(DataSecurityUtil.decrypt(tmp.getChargeEmail()));
                tmp.setChargeName(DataSecurityUtil.decrypt(tmp.getChargeName()));
            }
        }

        List<RootLine> rootLines;
        for(Root tmp:rootList){
            rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(RootLine.FIELD_ROOT_ID,tmp.getId())
                            .andEqualTo(RootLine.FIELD_PROJECT_ID,tmp.getProjectId())
                            .andEqualTo(RootLine.FIELD_TENANT_ID,tmp.getTenantId())
                    ).build());
            if(CollectionUtils.isNotEmpty(rootLines)){
                StringBuffer rootNameStr = new StringBuffer();
                rootLines.forEach(rootLine->{
                    rootNameStr.append(rootLine.getRootName()).append(" ");
                });
                tmp.setRootName(rootNameStr.toString());
            }
        }
        return rootList;
    }

    @Override
    public AssigneeUserDTO getAssigneeUser(Long chargeId) {
        AssigneeUserDTO assigneeUserDTO = rootMapper.getAssigneeUser(chargeId);
        //查询员工责任人并解密
        if (DataSecurityHelper.isTenantOpen()) {
            if (StringUtils.isNotEmpty(assigneeUserDTO.getEmployeeName())) {
                assigneeUserDTO.setEmployeeName(DataSecurityHelper.decrypt(assigneeUserDTO.getEmployeeName()));
            }
            if (StringUtils.isNotEmpty(assigneeUserDTO.getEmployeeNum())) {
                assigneeUserDTO.setEmployeeNum(DataSecurityHelper.decrypt(assigneeUserDTO.getEmployeeNum()));
            }
        }
        return assigneeUserDTO;
    }
}
