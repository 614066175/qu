package com.hand.hdsp.quality.infra.export;

import com.hand.hdsp.core.CommonGroupClient;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.RootGroupDTO;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.infra.export.dto.RootExportDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.ROOT_STANDARD;
import static org.hzero.core.base.BaseConstants.Symbol.COMMA;

import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/02/10 9:27
 */
@Component
public class RootExport implements Exporter<Root, List<RootExportDTO>>{
    private final CommonGroupRepository commonGroupRepository;
    private final RootRepository rootRepository;
    private final CommonGroupClient commonGroupClient;

    public RootExport(CommonGroupRepository commonGroupRepository, RootRepository rootRepository, CommonGroupClient commonGroupClient) {
        this.commonGroupRepository = commonGroupRepository;
        this.rootRepository = rootRepository;
        this.commonGroupClient = commonGroupClient;
    }

    @Override
    public List<RootExportDTO> export(Root root) {
        List<RootExportDTO> rootExportDTOS = null;
        //条件导出，导出选中id词根
        if(StringUtils.isNotEmpty(root.getExportIds())){
            //获取词根数据
            List<Long> exportIdList = Arrays.stream(root.getExportIds().split(COMMA)).map(Long::parseLong).collect(Collectors.toList());
            root.setExportIdList(exportIdList);
            List<Root> rootList = rootRepository.list(root);
            //获取对应分组信息
            List<Long> groupIdList = rootList.stream().map(Root::getGroupId).collect(Collectors.toList());
            List<CommonGroup> commonGroupList = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                    .andWhere(Sqls.custom().andIn(CommonGroup.FIELD_GROUP_ID,groupIdList)).build());
            rootExportDTOS = exportRootExportDTO(commonGroupList,rootList);
        }else{
            //导出目标分组数据或全部数据
            if(Objects.nonNull(root.getGroupId())){
                CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(root.getGroupId());
                List<CommonGroup> subGroup = commonGroupClient.getSubGroup(commonGroup);
                subGroup.add(commonGroup);
                Long[] groupIds = subGroup.stream().map(CommonGroup::getGroupId).toArray(Long[]::new);
                root.setGroupArrays(groupIds);
                root.setGroupId(null);
                List<Root> rootList = rootRepository.list(root);
                rootExportDTOS = exportRootExportDTO(subGroup,rootList);
            }else{
                List<CommonGroup> commonGroups = commonGroupRepository.selectByCondition(Condition.builder(CommonGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(CommonGroup.FIELD_TENANT_ID, root.getTenantId())
                                .andEqualTo(CommonGroup.FIELD_PROJECT_ID, root.getProjectId())
                                .andEqualTo(CommonGroup.FIELD_GROUP_TYPE, ROOT_STANDARD))
                        .build());
                List<Root> rootList = rootRepository.list(root);
                rootExportDTOS = exportRootExportDTO(commonGroups,rootList);
            }
        }
        return rootExportDTOS;
    }

    /**
     * 转换成导出的类
     * @param commonGroupList
     * @param rootList
     * @return
     */
    private List<RootExportDTO> exportRootExportDTO(List<CommonGroup> commonGroupList,List<Root> rootList){
        List<RootExportDTO> rootExportDTOS = new ArrayList<>();
        commonGroupList.forEach(commonGroup -> {
            int l = commonGroup.getGroupPath().lastIndexOf("/");
            if(l>0){
                commonGroup.setParentGroupPath(commonGroup.getGroupPath().substring(0,l));
            }
            RootExportDTO rootExportDTO = new RootExportDTO();
            BeanUtils.copyProperties(commonGroup, rootExportDTO);
            rootExportDTOS.add(rootExportDTO);
        });
        rootExportDTOS.get(0).setRoots(rootList);
        return rootExportDTOS;
    }
}
