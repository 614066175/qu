package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.app.service.StandardGroupService;
import org.xdsp.quality.domain.entity.*;
import org.xdsp.quality.domain.repository.*;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.StandardConstant;
import org.xdsp.quality.infra.mapper.DataStandardMapper;
import org.xdsp.quality.infra.mapper.StandardGroupMapper;
import org.xdsp.quality.infra.vo.StandardGroupVO;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 21:08
 * @since 1.0
 */
@Service
public class StandardGroupServiceImpl implements StandardGroupService {

    private final StandardGroupRepository standardGroupRepository;

    private final StandardGroupMapper standardGroupMapper;

    private final DataStandardRepository dataStandardRepository;

    private final StandardDocRepository standardDocRepository;

    private final DataStandardMapper dataStandardMapper;

    private final NameStandardRepository nameStandardRepository;

    private final DataFieldRepository dataFieldRepository;

    private final RootRepository rootRepository;

    public StandardGroupServiceImpl(StandardGroupRepository standardGroupRepository, StandardGroupMapper standardGroupMapper, DataStandardRepository dataStandardRepository, StandardDocRepository standardDocRepository, DataStandardMapper dataStandardMapper, NameStandardRepository nameStandardRepository, DataFieldRepository dataFieldRepository, RootRepository rootRepository) {
        this.standardGroupRepository = standardGroupRepository;
        this.standardGroupMapper = standardGroupMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.standardDocRepository = standardDocRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.nameStandardRepository = nameStandardRepository;
        this.dataFieldRepository = dataFieldRepository;
        this.rootRepository = rootRepository;
    }

    @Override
    public List<StandardGroupDTO> listByGroup(StandardGroupVO standardGroupVO) {
        return standardGroupMapper.listByGroup(standardGroupVO);
    }

    @Override
    public void delete(StandardGroupDTO standardGroupDTO) {
        //目录或子目录存在标准不可删除;不存在或存在空的分组则删除，并同时删除空的分组
        //遍历获取子目录集合
        List<StandardGroupDTO> groupLists = new ArrayList<>();
        findChildGroups(standardGroupDTO, groupLists);
        groupLists.add(standardGroupDTO);
        //判断是否包含标准
        groupLists.forEach(this::existStandard);
        standardGroupRepository.batchDTODeleteByPrimaryKey(groupLists);
    }

    private void findChildGroups(StandardGroupDTO standardGroupDTO, List<StandardGroupDTO> list) {
        List<StandardGroupDTO> standardGroups = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, standardGroupDTO.getGroupId())
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroups)) {
            list.addAll(standardGroups);
            standardGroups.forEach(standardGroupDto -> {
                findChildGroups(standardGroupDto, list);
            });
        }
    }

    private void existStandard(StandardGroupDTO standardGroupDTO) {
        String standardType = standardGroupDTO.getStandardType();
        switch (standardType) {
            case StandardConstant.StandardType.DATA:
                List<DataStandardDTO> dataStandardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataStandard.FIELD_GROUP_ID, standardGroupDTO.getGroupId())
                                .andEqualTo(DataStandard.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                                .andEqualTo(DataStandard.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(dataStandardDTOS)) {
                    StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByPrimaryKey(dataStandardDTOS.get(0).getGroupId());
                    throw new CommonException(ErrorCode.GROUP_HAS_STANDARD, groupDTO.getGroupName());
                }
                break;
            case StandardConstant.StandardType.FIELD:
                List<DataFieldDTO> dataFieldDTOS = dataFieldRepository.selectDTOByCondition(Condition.builder(DataField.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DataField.FIELD_GROUP_ID, standardGroupDTO.getGroupId())
                                .andEqualTo(DataField.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                                .andEqualTo(DataField.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(dataFieldDTOS)) {
                    StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByPrimaryKey(dataFieldDTOS.get(0).getGroupId());
                    throw new CommonException(ErrorCode.GROUP_HAS_STANDARD, groupDTO.getGroupName());
                }
                break;
            case StandardConstant.StandardType.NAME:
                List<NameStandardDTO> nameStandardDTOS = nameStandardRepository.selectDTOByCondition(Condition.builder(NameStandard.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(NameStandard.FIELD_GROUP_ID, standardGroupDTO.getGroupId())
                                .andEqualTo(NameStandard.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                                .andEqualTo(NameStandard.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(nameStandardDTOS)) {
                    StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByPrimaryKey(nameStandardDTOS.get(0).getGroupId());
                    throw new CommonException(ErrorCode.GROUP_HAS_STANDARD, groupDTO.getGroupName());
                }
                break;
            case StandardConstant.StandardType.DOC:
                List<StandardDocDTO> standardDocDTOS = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardDoc.FIELD_GROUP_ID, standardGroupDTO.getGroupId())
                                .andEqualTo(StandardDoc.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                                .andEqualTo(StandardDoc.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(standardDocDTOS)) {
                    StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByPrimaryKey(standardDocDTOS.get(0).getGroupId());
                    throw new CommonException(ErrorCode.GROUP_HAS_STANDARD, groupDTO.getGroupName());
                }
                break;
            case StandardConstant.StandardType.ROOT:
                List<Root> rootList = rootRepository.selectByCondition(Condition.builder(Root.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(Root.FIELD_GROUP_ID, standardGroupDTO.getGroupId())
                                .andEqualTo(Root.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                                .andEqualTo(Root.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(rootList)) {
                    StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByPrimaryKey(rootList.get(0).getGroupId());
                    throw new CommonException(ErrorCode.GROUP_HAS_ROOT, groupDTO.getGroupName());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public Page<T> list(PageRequest pageRequest, StandardGroupVO standardGroupVO) {
        //数据标准
        if ("DATA".equals(standardGroupVO.getStandardType())) {
            Condition condition = Condition.builder(DataStandard.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(DataStandard.FIELD_GROUP_ID, standardGroupVO.getGroupId()))
                    .build();
            return PageHelper.doPageAndSort(pageRequest, () -> dataStandardRepository.selectDTOByCondition(condition));
        }
        //字段标准
        //命名标准
        //标准文档
        return null;
    }

    @Override
    public List<StandardGroupDTO> export(StandardGroupDTO dto, ExportParam exportParam) {
        List<StandardGroupDTO> standardGroupDTOS = null;
        if (StandardConstant.StandardType.DATA.equals(dto.getStandardType())) {
            //获取DATA所有的分组已经分组下的标准
            standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardGroup.FIELD_PROJECT_ID, dto.getProjectId())
                            .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, StandardConstant.StandardType.DATA)
                            .andEqualTo(StandardGroup.FIELD_TENANT_ID, dto.getTenantId()))
                    .build());
            if (CollectionUtils.isNotEmpty(standardGroupDTOS)) {
                standardGroupDTOS.forEach(standardGroupDTO -> {
                    List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(DataStandard.FIELD_PROJECT_ID, dto.getProjectId())
                                    .andEqualTo(DataStandard.FIELD_GROUP_ID, standardGroupDTO.getGroupId())
                                    .andEqualTo(DataStandard.FIELD_TENANT_ID, dto.getTenantId()))
                            .build());
                    //查询负责人名称和负责部门
                    dataStandardDTOList.forEach(dataStandardDTO -> {
                        dataStandardDTO.setChargeName(dataStandardMapper.selectChargeNameById(dataStandardDTO.getChargeId()));
                        dataStandardDTO.setChargeDeptName(dataStandardMapper.selectChargeDeptNameById(dataStandardDTO.getChargeDeptId()));
                    });
                    standardGroupDTO.setDataStandardDTOList(dataStandardDTOList);
                    //是否排除没有内容的分组导出
                });
            }
        }
        return standardGroupDTOS;
    }

    @Override
    public int create(StandardGroupDTO standardGroupDTO) {
        // 校验父目录下是否有标准
//        if (standardGroupDTO.getParentGroupId() != null) {
//            StandardGroupDTO dto = standardGroupRepository.selectDTOByPrimaryKey(standardGroupDTO.getParentGroupId());
//            existStandard(dto);
//        }
        // 校验编码存在
        List<StandardGroupDTO> dtoList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, standardGroupDTO.getStandardType())
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, standardGroupDTO.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        // 校验名称存在
        dtoList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, standardGroupDTO.getStandardType())
                        .andEqualTo(StandardGroup.FIELD_GROUP_NAME, standardGroupDTO.getGroupName())
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID, standardGroupDTO.getProjectId())
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, standardGroupDTO.getParentGroupId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.GROUP_NAME_ALREADY_EXIST);
        }
        return standardGroupRepository.insertDTOSelective(standardGroupDTO);
    }

    @Override
    public StandardGroupDTO update(StandardGroupDTO standardGroupDTO) {
        List<StandardGroupDTO> dtoList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, standardGroupDTO.getStandardType())
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                        .andEqualTo(StandardGroup.FIELD_GROUP_NAME, standardGroupDTO.getGroupName())
                        .andEqualTo(StandardGroup.FIELD_PROJECT_ID, standardGroupDTO.getProjectId()))
                .build());
        if (dtoList.size() > 1 || (dtoList.size() == 1 && !dtoList.get(0).getGroupCode().equals(standardGroupDTO.getGroupCode()))) {
            throw new CommonException(ErrorCode.GROUP_NAME_ALREADY_EXIST);
        }
        standardGroupRepository.updateDTOAllColumnWhereTenant(standardGroupDTO, standardGroupDTO.getTenantId());
        return standardGroupRepository.selectDTOByPrimaryKeyAndTenant(standardGroupDTO);
    }
}
