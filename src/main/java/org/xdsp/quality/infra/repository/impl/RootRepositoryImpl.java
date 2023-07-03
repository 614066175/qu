package org.xdsp.quality.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.core.util.DataSecurityUtil;
import org.xdsp.quality.api.dto.AssigneeUserDTO;
import org.xdsp.quality.api.dto.RootDTO;
import org.xdsp.quality.domain.entity.Root;
import org.xdsp.quality.domain.entity.RootLine;
import org.xdsp.quality.domain.repository.RootLineRepository;
import org.xdsp.quality.domain.repository.RootRepository;
import org.xdsp.quality.infra.constant.StandardConstant;
import org.xdsp.quality.infra.mapper.RootMapper;

import java.util.List;
import java.util.stream.Collectors;

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
            if (StringUtils.isNotEmpty(root.getChargeName())) {
                root.setChargeName(DataSecurityUtil.encrypt(root.getChargeName()));
            }
        }

        List<Root> rootList = rootMapper.list(root);
        for (Root rootTemp : rootList) {
            decodeForRoot(rootTemp);
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
                List<String> str = rootLines.stream().map(RootLine::getRootName).collect(Collectors.toList());
                String rootNameStr = StringUtils.join(str,StandardConstant.RootName.SEPARATOR);
                tmp.setRootName(rootNameStr);
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

    /**
     * 解密字段
     *
     * @param root
     */
    private void decodeForRoot(Root root) {
        if (DataSecurityHelper.isTenantOpen()) {
            // 责任人等
            if (StringUtils.isNotEmpty(root.getChargeDept())) {
                root.setChargeDept(DataSecurityHelper.decrypt(root.getChargeDept()));
            }
            if (StringUtils.isNotEmpty(root.getChargeTel())) {
                root.setChargeTel(DataSecurityHelper.decrypt(root.getChargeTel()));
            }
            if (StringUtils.isNotEmpty(root.getChargeEmail())) {
                root.setChargeEmail(DataSecurityHelper.decrypt(root.getChargeEmail()));
            }
            if (StringUtils.isNotEmpty(root.getChargeName())) {
                root.setChargeName(DataSecurityHelper.decrypt(root.getChargeName()));
            }
        }
    }
}
