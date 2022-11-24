package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.entity.RootLine;
import com.hand.hdsp.quality.domain.entity.RootVersion;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.RootLineRepository;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.domain.repository.RootVersionRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.ROOT;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * 词根应用服务默认实现
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@Service
public class RootServiceImpl implements RootService {

    private final RootRepository rootRepository;
    private final RootVersionRepository rootVersionRepository;
    private final RootLineRepository rootLineRepository;
    private final StandardGroupRepository standardGroupRepository;

    private final String PATTERN = "^[A-Za-z][A-Za-z0-9]{0,63}$";

    public RootServiceImpl(RootRepository rootRepository, RootVersionRepository rootVersionRepository, RootLineRepository rootLineRepository, StandardGroupRepository standardGroupRepository) {
        this.rootRepository = rootRepository;
        this.rootVersionRepository = rootVersionRepository;
        this.rootLineRepository = rootLineRepository;
        this.standardGroupRepository = standardGroupRepository;
    }

    @Override
    public Page<Root> list(PageRequest pageRequest, Root root) {
        return PageHelper.doPageAndSort(pageRequest,()->rootRepository.list(root));
    }

    @Override
    public Root detail(Long id) {
        List<Root> list = rootRepository.list(Root.builder().id(id).build());
        Root root = null;
        if(CollectionUtils.isNotEmpty(list)){
            root = list.get(0);
        }
        return root;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Root root) {
        if(!Pattern.matches(PATTERN, root.getRootEn())){
            throw new CommonException(ErrorCode.ROOT_EN_SHORT_ERROR);
        }

        List<Root> rootList = rootRepository.selectByCondition(Condition.builder(Root.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Root.FIELD_ROOT_EN_SHORT,root.getRootEnShort())
                        .andEqualTo(Root.FIELD_PROJECT_ID,root.getProjectId())
                        .andEqualTo(Root.FIELD_TENANT_ID,root.getTenantId())
                ).build());
        if(CollectionUtils.isNotEmpty(rootList)){
            throw new CommonException(ErrorCode.ROOT_EN_SHORT_EXIST);
        }

        //词根中文校验
        String[] rootName =  root.getRootName().split(StandardConstant.RootName.SEPARATOR);

        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom().andIn(RootLine.FIELD_ROOT_NAME,Arrays.asList(rootName))
                        .andEqualTo(RootLine.FIELD_PROJECT_ID,root.getProjectId())
                        .andEqualTo(RootLine.FIELD_TENANT_ID,root.getTenantId())
                ).build());

        if(CollectionUtils.isNotEmpty(rootLines)){
            StringBuffer rootNameStr = new StringBuffer();
            for (RootLine rootLine: rootLines){
                rootNameStr.append(rootLine.getRootName()).append(" ");
            }
            throw new CommonException(ErrorCode.ROOT_NAME_EXIST,rootNameStr);
        }

        root.setReleaseStatus(StandardConstant.Status.CREATE);
        rootRepository.insertSelective(root);
        rootLines = new ArrayList<>();
        RootLine rootLine;
        for(int i=0;i<rootName.length;i++){
            rootLine = RootLine.builder()
                    .rootId(root.getId())
                    .rootName(rootName[i])
                    .projectId(root.getProjectId())
                    .tenantId(root.getTenantId())
                    .build();
            rootLines.add(rootLine);
        }
        rootLineRepository.batchInsert(rootLines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Root root) {
        rootRepository.updateByPrimaryKeySelective(root);

        //删除行表数据，重新插入
        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootLine.FIELD_ROOT_ID,root.getId())
                        .andEqualTo(RootLine.FIELD_PROJECT_ID,root.getProjectId())
                        .andEqualTo(RootLine.FIELD_TENANT_ID,root.getTenantId())
                ).build());
        rootLineRepository.batchDelete(rootLines);

        String[] rootName =  root.getRootName().split(StandardConstant.RootName.SEPARATOR);
        rootLines = new ArrayList<>();
        for(int i=0;i<rootName.length;i++){
            RootLine rootLine = RootLine.builder()
                    .rootId(root.getId())
                    .rootName(rootName[i])
                    .projectId(root.getProjectId())
                    .tenantId(root.getTenantId())
                    .build();
            rootLines.add(rootLine);
        }
        rootLineRepository.batchInsert(rootLines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Root root) {
        if (ONLINE.equals(root.getReleaseStatus())
                || OFFLINE_APPROVING.equals(root.getReleaseStatus())
                || ONLINE_APPROVING.equals(root.getReleaseStatus())) {
            throw new CommonException(ErrorCode.ROOT_NOT_DELETE);
        }
        rootRepository.deleteByPrimaryKey(root);

        //删除行表数据
        List<RootLine> rootLines = rootLineRepository.selectByCondition(Condition.builder(RootLine.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootLine.FIELD_ROOT_ID,root.getId())
                        .andEqualTo(RootLine.FIELD_PROJECT_ID,root.getProjectId())
                        .andEqualTo(RootLine.FIELD_TENANT_ID,root.getTenantId())
                ).build());
        rootLineRepository.batchDelete(rootLines);

        //删除版本表数据
        List<RootVersion> rootVersions = rootVersionRepository.selectByCondition(Condition.builder(RootVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootVersion.FIELD_ROOT_ID,root.getId())
                        .andEqualTo(RootVersion.FIELD_PROJECT_ID,root.getProjectId())
                        .andEqualTo(RootVersion.FIELD_TENANT_ID,root.getTenantId())
                ).build());
        if(CollectionUtils.isNotEmpty(rootVersions)){
            rootVersionRepository.batchDelete(rootVersions);
        }
    }

    @Override
    public List<RootGroupDTO> export(Root root, ExportParam exportParam) {
        List<RootGroupDTO> rootGroupDTOS = new ArrayList<>();
        int level = 1;
        if(ObjectUtils.isNotEmpty(root.getGroupId())){
            //分组条件导出
            StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                    .andEqualTo(StandardGroup.FIELD_TENANT_ID, root.getTenantId())
                    .andEqualTo(StandardGroup.FIELD_PROJECT_ID, root.getProjectId())
                    .andEqualTo(StandardGroup.FIELD_GROUP_ID, root.getGroupId())
            ).build()).get(0);
            //获取设置当前分组的父分组编码
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(groupDTO.getParentGroupId());
                groupDTO.setParentGroupCode(parentGroupDTO.getGroupCode());
            }
            //查询当前分组数据
            List<Root> roots = rootRepository.list(Root.builder().groupId(groupDTO.getGroupId()).build());
            RootGroupDTO dto = new RootGroupDTO();
            BeanUtils.copyProperties(groupDTO, dto);
            dto.setRoots(roots);
            dto.setGroupLevel(level);
            rootGroupDTOS.add(dto);
            //添加查询父分组 并排序
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                findParentGroups(groupDTO.getParentGroupId(), rootGroupDTOS, level);
            }
            findChildGroups(dto,rootGroupDTOS,level);
            return rootGroupDTOS.stream().sorted(Comparator.comparing(RootGroupDTO::getGroupLevel)).collect(Collectors.toList());
        }else {
            //全部分组条件导出
            //添加查询所有父分组 并排序导出保证导入准确性
            List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                            .andEqualTo(StandardGroup.FIELD_TENANT_ID, root.getTenantId())
                            .andEqualTo(StandardGroup.FIELD_PROJECT_ID, root.getProjectId())
                            .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, ROOT)
                            .andIsNull(StandardGroup.FIELD_PARENT_GROUP_ID))
                    .build());
            standardGroupDTOList.forEach(standardGroupDTO -> {
                //从所有的根目录 向下查询
                RootGroupDTO dto = new RootGroupDTO();
                BeanUtils.copyProperties(standardGroupDTO, dto);
                //查询当前分组数据
                List<Root> roots = rootRepository.list(Root.builder().groupId(standardGroupDTO.getGroupId()).build());
                dto.setRoots(roots);
                dto.setGroupLevel(level);
                rootGroupDTOS.add(dto);
                findChildGroups(dto,rootGroupDTOS,level);
            });
            return rootGroupDTOS.stream().sorted(Comparator.comparing(RootGroupDTO::getGroupLevel)).collect(Collectors.toList());
        }
    }

    private void findParentGroups(Long groupId, List<RootGroupDTO> rootGroupDTOs, int level) {
        RootGroupDTO rootGroupDTO = new RootGroupDTO();
        StandardGroupDTO  standardGroupDTO= standardGroupRepository.selectDTOByPrimaryKey(groupId);
        level--;

        if(ObjectUtils.isNotEmpty(standardGroupDTO.getParentGroupId())){
            StandardGroupDTO parentGroupDTO= standardGroupRepository.selectDTOByPrimaryKey(standardGroupDTO.getParentGroupId());
            standardGroupDTO.setParentGroupCode(parentGroupDTO.getGroupCode());
            findParentGroups(standardGroupDTO.getParentGroupId(), rootGroupDTOs, level);
        }
        BeanUtils.copyProperties(standardGroupDTO, rootGroupDTO);
        rootGroupDTO.setGroupLevel(level);
        rootGroupDTOs.add(rootGroupDTO);
    }

    private void findChildGroups(RootGroupDTO parentRootGroupDTO,List<RootGroupDTO> rootGroupDTOs, int level) {
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, parentRootGroupDTO.getGroupId()))
                .build());
        level++;
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(standardGroupDTO -> {
                RootGroupDTO rootGroupDTO = new RootGroupDTO();
                BeanUtils.copyProperties(standardGroupDTO, rootGroupDTO);
                rootGroupDTO.setGroupLevel(finalLevel);
                rootGroupDTO.setParentGroupCode(parentRootGroupDTO.getGroupCode());
                List<Root> roots = rootRepository.list(Root.builder().groupId(rootGroupDTO.getGroupId()).build());
                rootGroupDTO.setRoots(roots);
                rootGroupDTOs.add(rootGroupDTO);
                findChildGroups(rootGroupDTO,rootGroupDTOs, finalLevel);
            });
        }
    }
}
