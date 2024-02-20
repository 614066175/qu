package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.util.PageParseUtil;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.StandardTeamDTO;
import org.xdsp.quality.api.dto.TableColumnDTO;
import org.xdsp.quality.app.service.StandardTeamService;
import org.xdsp.quality.domain.entity.StandardRelation;
import org.xdsp.quality.domain.entity.StandardTeam;
import org.xdsp.quality.domain.repository.DataFieldRepository;
import org.xdsp.quality.domain.repository.StandardRelationRepository;
import org.xdsp.quality.domain.repository.StandardTeamRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.feign.ModelFeign;
import org.xdsp.quality.infra.mapper.DataFieldMapper;
import org.xdsp.quality.infra.mapper.StandardTeamMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>标准表应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Service
@Slf4j
public class StandardTeamServiceImpl implements StandardTeamService {
    private final StandardTeamRepository standardTeamRepository;
    private final StandardTeamMapper standardTeamMapper;
    private final StandardRelationRepository standardRelationRepository;

    private final DataFieldMapper dataFieldMapper;
    private final DataFieldRepository dataFieldRepository;
    private final ModelFeign modelFeign;

    public StandardTeamServiceImpl(StandardTeamRepository standardTeamRepository, StandardTeamMapper standardTeamMapper, StandardRelationRepository standardRelationRepository, DataFieldMapper dataFieldMapper, DataFieldRepository dataFieldRepository, ModelFeign modelFeign) {
        this.standardTeamRepository = standardTeamRepository;
        this.standardTeamMapper = standardTeamMapper;
        this.standardRelationRepository = standardRelationRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataFieldRepository = dataFieldRepository;
        this.modelFeign = modelFeign;
    }

    @Override
    public Page<StandardTeamDTO> list(PageRequest pageRequest, StandardTeamDTO standardTeamDTO) {
        return PageHelper.doPage(pageRequest, () -> standardTeamMapper.listAll(standardTeamDTO));
    }

    /**
     * 查询全部数据
     *
     * @param standardTeamDTO
     * @return
     */
    @Override
    public List<StandardTeamDTO> listAll(StandardTeamDTO standardTeamDTO) {
        return standardTeamMapper.listAll(standardTeamDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long standardTeamId) {
        StandardTeamDTO standardTeamDTO = standardTeamRepository.selectDTOByPrimaryKey(standardTeamId);
        if (standardTeamDTO == null) {
            throw new CommonException(ErrorCode.STANDARD_TEAM_NOT_EXIST);
        }
        //（删除的字段标准组下若有子级字段标准组，子级字段标准组更新为顶层字段标准组，即父级字段标准组字段清空；
        // 存在其他字段标准组的继承自为当前删除行字段标准组时，提示“当前字段标准组已被其他字段标准组继承，删除失败，请确认”）
        List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByCondition(Condition.builder(StandardTeam.class)
                .andWhere(Sqls.custom().andEqualTo(StandardTeam.FIELD_INHERITE_TEAM_ID, standardTeamId))
                .build());
        // 存在其他字段标准组的继承自为当前删除行字段标准组时，提示“当前字段标准组已被其他字段标准组继承，删除失败，请确认”）
        if (CollectionUtils.isNotEmpty(standardTeamDTOS)) {
            throw new CommonException(ErrorCode.STANDARD_TEAM_IS_INHERITED);
        }


        //子标准组
        List<StandardTeam> sonStandardTeamList = standardTeamRepository.selectByCondition(Condition.builder(StandardTeam.class)
                .andWhere(Sqls.custom().andEqualTo(StandardTeam.FIELD_PARENT_TEAM_ID, standardTeamId))
                .build());

        //删除的字段标准组下若有子级字段标准组，子级字段标准组更新为顶层字段标准组，即父级字段标准组字段清空；
        if (CollectionUtils.isNotEmpty(sonStandardTeamList)) {
            sonStandardTeamList.forEach(sonStandardTeam -> sonStandardTeam.setParentTeamId(null));
            standardTeamRepository.batchUpdateOptional(sonStandardTeamList, StandardTeam.FIELD_PARENT_TEAM_ID);
        }
        //删除此标准的所有标准关系
        List<StandardRelation> standardRelations = standardRelationRepository.selectByCondition(Condition.builder(StandardRelation.class)
                .andWhere(Sqls.custom().andEqualTo(StandardRelation.FIELD_STANDARD_TEAM_ID, standardTeamDTO.getStandardTeamId())
                        .andEqualTo(StandardRelation.FIELD_TENANT_ID, standardTeamDTO.getTenantId())
                        .andEqualTo(StandardRelation.FIELD_PROJECT_ID, standardTeamDTO.getProjectId()))
                .build());
        standardTeamRepository.deleteByPrimaryKey(standardTeamDTO);
        standardRelationRepository.batchDeleteByPrimaryKey(standardRelations);
    }

    @Override
    @SuppressWarnings(value = "all")
    public Page<DataFieldDTO> fieldStandardList(DataFieldDTO dataFieldDTO, PageRequest pageRequest) {
        //如果标准组id和继承自id为空时，则是新建并且没有选择继承自其他的标准组，即查询分页查询字段标准即可
        if (dataFieldDTO == null ||
                (dataFieldDTO.getInheriteTeamId() == null && dataFieldDTO.getStandardTeamId() == null)) {
            return PageHelper.doPage(pageRequest, () -> dataFieldMapper.list(dataFieldDTO));
        }

        //查询所有字段标准
        List<DataFieldDTO> allDataFieldDTOList = dataFieldMapper.list(dataFieldDTO);
        if (CollectionUtils.isEmpty(allDataFieldDTOList)) {
            log.info("没有字段标准");
            return new Page<>();

        }
        //新建时选择了继承自
        if (dataFieldDTO.getStandardTeamId() == null && dataFieldDTO.getInheriteTeamId() != null) {
            //新建是通过继承自的标准组来查询
            StandardTeamDTO inheriteStandardTeamDTO = standardTeamRepository.selectDTOByPrimaryKey(dataFieldDTO.getInheriteTeamId());
            //查询此继承自的标准组的所有标准关系
            List<StandardRelation> inheriteStandardRelations = standardRelationRepository.selectByCondition(Condition.builder(StandardRelation.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardRelation.FIELD_STANDARD_TEAM_ID, inheriteStandardTeamDTO.getStandardTeamId())
                            .andEqualTo(StandardRelation.FIELD_TENANT_ID, inheriteStandardTeamDTO.getTenantId())
                            .andEqualTo(StandardRelation.FIELD_PROJECT_ID, inheriteStandardTeamDTO.getProjectId()))
                    .build());

            if (CollectionUtils.isEmpty(inheriteStandardRelations)) {
                //如果此标准组没有字段标准，则直接分页查询字段标准
                return PageHelper.doPage(pageRequest, () -> dataFieldMapper.list(dataFieldDTO));
            }
            //此继承自标准组下的字段标准
            List<Long> inheriteFieldStandardIds = inheriteStandardRelations.stream()
                    .map(StandardRelation::getFieldStandardId)
                    .collect(Collectors.toList());

            //看那些标准是被选中的,并且是被继承的
            for (DataFieldDTO dto : allDataFieldDTOList) {
                if (inheriteFieldStandardIds.contains(dto.getFieldId())) {
                    //被继承的字段不可编辑，且被选中
                    dto.setEditFlag(0);
                    dto.setCheckFlag(1);
                } else {
                    dto.setEditFlag(1);
                    dto.setCheckFlag(0);
                }
            }
        } else {
            StandardTeamDTO standardTeamDTO = standardTeamRepository.selectDTOByPrimaryKey(dataFieldDTO.getStandardTeamId());
            //查询此标准组的所有标准关系
            List<StandardRelation> standardRelations = standardRelationRepository.selectByCondition(Condition.builder(StandardRelation.class)
                    .andWhere(Sqls.custom().andEqualTo(StandardRelation.FIELD_STANDARD_TEAM_ID, standardTeamDTO.getStandardTeamId())
                            .andEqualTo(StandardRelation.FIELD_TENANT_ID, standardTeamDTO.getTenantId())
                            .andEqualTo(StandardRelation.FIELD_PROJECT_ID, standardTeamDTO.getProjectId()))
                    .build());
            //如果此标准组没有字段标准，且没有继承自，则直接分页查询字段标准
            if (CollectionUtils.isEmpty(standardRelations) && dataFieldDTO.getInheriteTeamId() == null) {
                return PageHelper.doPage(pageRequest, () -> dataFieldMapper.list(dataFieldDTO));
            }
            //此标准组下的字段标准
            List<Long> fieldStandardIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(standardRelations)) {
                fieldStandardIds = standardRelations.stream()
                        .map(StandardRelation::getFieldStandardId)
                        .collect(Collectors.toList());
            }

            List<Long> inheriteFieldStandardIds = new ArrayList<>();
            //如果有继承自，则再去查询继承自的情况
            if (dataFieldDTO.getInheriteTeamId() != null) {
                StandardTeamDTO inheriteTeamDTO = standardTeamRepository.selectDTOByPrimaryKey(dataFieldDTO.getInheriteTeamId());
                //查询此继承标准组的所有标准关系
                List<StandardRelation> inheriteRelations = standardRelationRepository.selectByCondition(Condition.builder(StandardRelation.class)
                        .andWhere(Sqls.custom().andEqualTo(StandardRelation.FIELD_STANDARD_TEAM_ID, inheriteTeamDTO.getStandardTeamId())
                                .andEqualTo(StandardRelation.FIELD_TENANT_ID, inheriteTeamDTO.getTenantId())
                                .andEqualTo(StandardRelation.FIELD_PROJECT_ID, inheriteTeamDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(inheriteRelations)) {
                    //继承的字段标准id
                    inheriteFieldStandardIds = inheriteRelations.stream()
                            .map(StandardRelation::getFieldStandardId)
                            .collect(Collectors.toList());
                    //合并继承的标准
                    fieldStandardIds.addAll(inheriteFieldStandardIds);
                }
            }

            //看那些标准是被选中的,并且是被继承的
            for (DataFieldDTO dto : allDataFieldDTOList) {
                if (fieldStandardIds.contains(dto.getFieldId())) {
                    dto.setCheckFlag(1);
                } else {
                    dto.setCheckFlag(0);
                }
                if (inheriteFieldStandardIds.contains(dto.getFieldId())) {
                    //被继承的字段不可编辑
                    dto.setEditFlag(0);
                } else {
                    dto.setEditFlag(1);
                }
            }
        }
        //若责任人信息加密则解密
        if (DataSecurityHelper.isTenantOpen()) {
            allDataFieldDTOList.forEach(dataFieldDto -> {
                if (StringUtils.isNotEmpty(dataFieldDto.getChargeName())) {
                    dataFieldDto.setChargeName(DataSecurityHelper.decrypt(dataFieldDto.getChargeName()));
                }
                if (StringUtils.isNotEmpty(dataFieldDto.getChargeTel())) {
                    dataFieldDto.setChargeTel(DataSecurityHelper.decrypt(dataFieldDto.getChargeTel()));
                }
                if (StringUtils.isNotEmpty(dataFieldDto.getChargeEmail())) {
                    dataFieldDto.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDto.getChargeEmail()));
                }
                if (StringUtils.isNotEmpty(dataFieldDto.getChargeDeptName())) {
                    dataFieldDto.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDto.getChargeDeptName()));
                }
            });
        }
        //先按是否选中降序，再按是否可编辑升序（继承的放前面），再按字段主键降序
        List<DataFieldDTO> sortedList = allDataFieldDTOList.stream()
                .sorted(Comparator.comparing(DataFieldDTO::getCheckFlag, Comparator.reverseOrder())
                        .thenComparing(DataFieldDTO::getEditFlag)
                        .thenComparing(DataFieldDTO::getFieldId, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        org.springframework.data.domain.Page<DataFieldDTO> page = PageUtil.doPage(sortedList, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize()));
        return PageParseUtil.springPage2C7nPage(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StandardTeamDTO create(StandardTeamDTO standardTeamDTO) {
        //创建标准组
        standardTeamRepository.insertDTOSelective(standardTeamDTO);

        //如果创建的时候有字段标准，则维护字段标准与标准组的关系
        List<StandardRelation> standardRelations = convertStandardRelation(standardTeamDTO);
        standardRelationRepository.batchInsert(standardRelations);
        return standardTeamDTO;
    }

    @Override
    public StandardTeamDTO detail(Long standardTeamId) {
        StandardTeamDTO standardTeamDTO = standardTeamMapper.detail(standardTeamId);
        if (standardTeamDTO == null) {
            throw new CommonException(ErrorCode.STANDARD_TEAM_NOT_EXIST);
        }
        List<StandardRelation> standardRelations = standardRelationRepository.selectByCondition(Condition.builder(StandardRelation.class)
                .andWhere(Sqls.custom().andEqualTo(StandardRelation.FIELD_STANDARD_TEAM_ID, standardTeamDTO.getStandardTeamId())
                        .andEqualTo(StandardRelation.FIELD_TENANT_ID, standardTeamDTO.getTenantId())
                        .andEqualTo(StandardRelation.FIELD_PROJECT_ID, standardTeamDTO.getProjectId()))
                .build());
        //如果继承自不为空,获取继承自的字段集合
        StandardTeamDTO inheriteTeamDTO;
        List<Long> inheriteFieldIds = null;
        if (standardTeamDTO.getInheriteTeamId() != null) {
            inheriteTeamDTO = standardTeamRepository.selectDTOByPrimaryKey(standardTeamDTO.getInheriteTeamId());
            if (inheriteTeamDTO != null) {
                List<StandardRelation> inheriteRelations = standardRelationRepository.selectByCondition(Condition.builder(StandardRelation.class)
                        .andWhere(Sqls.custom().andEqualTo(StandardRelation.FIELD_STANDARD_TEAM_ID, inheriteTeamDTO.getStandardTeamId())
                                .andEqualTo(StandardRelation.FIELD_TENANT_ID, inheriteTeamDTO.getTenantId())
                                .andEqualTo(StandardRelation.FIELD_PROJECT_ID, inheriteTeamDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(inheriteRelations)) {
                    inheriteFieldIds = inheriteRelations.stream().map(StandardRelation::getFieldStandardId).collect(Collectors.toList());
                    standardRelations.addAll(inheriteRelations);
                    //去重
                    standardRelations = new ArrayList<>(standardRelations.stream()
                            .collect(Collectors.toMap(StandardRelation::getFieldStandardId, s -> s, (s1, s2) -> s1))
                            .values());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(standardRelations)) {
            List<Long> finalInheriteFieldIds = inheriteFieldIds;
            List<DataFieldDTO> dataFieldDTOList = standardRelations.stream()
                    .map(standardRelation -> {
                        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(standardRelation.getFieldStandardId());
                        dataFieldDTO.setCheckFlag(1);
                        //记录标准关系id，用于批量移除
                        dataFieldDTO.setStandardRelationId(standardRelation.getRelationId());
                        if (CollectionUtils.isNotEmpty(finalInheriteFieldIds)) {
                            if (finalInheriteFieldIds.contains(dataFieldDTO.getFieldId())) {
                                dataFieldDTO.setEditFlag(0);
                            }
                        }
                        return dataFieldDTO;
                    })
                    .sorted(Comparator.comparing(DataFieldDTO::getEditFlag)
                            .thenComparing(DataFieldDTO::getFieldId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
            standardTeamDTO.setDataFieldDTOList(dataFieldDTOList);
        }
        return standardTeamDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StandardTeamDTO update(StandardTeamDTO standardTeamDTO) {
        //更新标准组
        standardTeamRepository.updateDTOAllColumnWhereTenant(standardTeamDTO, standardTeamDTO.getTenantId());
        //清空之前此标准组下的标准关系
        List<StandardRelation> oldStandardRelations = standardRelationRepository.selectByCondition(Condition.builder(StandardRelation.class)
                .andWhere(Sqls.custom().andEqualTo(StandardRelation.FIELD_STANDARD_TEAM_ID, standardTeamDTO.getStandardTeamId())
                        .andEqualTo(StandardRelation.FIELD_TENANT_ID, standardTeamDTO.getTenantId())
                        .andEqualTo(StandardRelation.FIELD_PROJECT_ID, standardTeamDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(oldStandardRelations)) {
            standardRelationRepository.batchDeleteByPrimaryKey(oldStandardRelations);
        }
        //如果更新的时候有字段标准，则维护字段标准与标准组的关系
        List<StandardRelation> standardRelations = convertStandardRelation(standardTeamDTO);
        standardRelationRepository.batchInsert(standardRelations);
        return standardTeamDTO;
    }

    @Override
    public List<DataFieldDTO> standardByTeamId(Long standardTeamId) {
        StandardTeamDTO standardTeamDTO = detail(standardTeamId);
        return standardTeamDTO.getDataFieldDTOList() == null ? new ArrayList<>() : standardTeamDTO.getDataFieldDTOList();
    }

    @Override
    public Page<StandardTeamDTO> parentTeamList(Long standardTeamId, PageRequest pageRequest) {
        if (standardTeamId == null) {
            return PageHelper.doPage(pageRequest, () -> standardTeamMapper.listAll(StandardTeamDTO.builder()
                    .tenantId(DetailsHelper.getUserDetails().getTenantId())
                    .projectId(ProjectHelper.getProjectId())
                    .build()));
        }
        //递归查找到此标准组下的标准组
        List<StandardTeam> subStandardTeamList = getSubStandardTeam(standardTeamId);
        List<StandardTeamDTO> standardTeamDTOS = standardTeamMapper.listAll(StandardTeamDTO.builder()
                .tenantId(DetailsHelper.getUserDetails().getTenantId())
                .projectId(ProjectHelper.getProjectId())
                .build());
        List<Long> subStandardTeamIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(standardTeamDTOS)) {
            subStandardTeamIds = subStandardTeamList.stream()
                    .map(StandardTeam::getStandardTeamId).collect(Collectors.toList());
        }
        //并且加上自己
        subStandardTeamIds.add(standardTeamId);
        List<Long> finalSubStandardTeamIds = subStandardTeamIds;
        List<StandardTeamDTO> filterStandardTeam = standardTeamDTOS.stream()
                .filter(standardTeamDTO -> !finalSubStandardTeamIds.contains(standardTeamDTO.getStandardTeamId()))
                .collect(Collectors.toList());

        org.springframework.data.domain.Page<StandardTeamDTO> page = PageUtil.doPage(filterStandardTeam, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize()));
        return PageParseUtil.springPage2C7nPage(page);
    }

    @Override
    public Page<DataFieldDTO> standardList(DataFieldDTO dataFieldDTO, PageRequest pageRequest) {
        if (dataFieldDTO.getCustomTableId() != null) {
            //从表设计查询标准组
            List<TableColumnDTO> tableColumnDTOS = dataFieldMapper.selectStandardColumn(dataFieldDTO.getCustomTableId());
            if (CollectionUtils.isNotEmpty(tableColumnDTOS)) {
                List<Long> checkFieldIds = tableColumnDTOS.stream().map(TableColumnDTO::getQuoteId).collect(Collectors.toList());
                dataFieldDTO.setCheckFieldIds(checkFieldIds);
            }
        }
        if (dataFieldDTO.getStandardTeamId() == null) {
            //如果没有传递标准组参数，则就是字段标准的列表查询
            return PageHelper.doPage(pageRequest, () -> dataFieldRepository.list(dataFieldDTO));
        }
        //查询此标准组下的字段标准
        StandardTeamDTO standardTeamDTO = detail(dataFieldDTO.getStandardTeamId());
        if (CollectionUtils.isEmpty(standardTeamDTO.getDataFieldDTOList())) {
            return new Page<>();
        }
        List<DataFieldDTO> dataFieldDTOList = standardTeamDTO.getDataFieldDTOList();
        if (StringUtils.isNotEmpty(dataFieldDTO.getFieldName())) {
            dataFieldDTOList = dataFieldDTOList.stream()
                    .filter(dto -> dto.getFieldName().contains(dataFieldDTO.getFieldName()))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(dataFieldDTO.getFieldComment())) {
            dataFieldDTOList = dataFieldDTOList.stream()
                    .filter(dto -> StringUtils.isNotEmpty(dto.getFieldComment())
                            && dto.getFieldComment().contains(dataFieldDTO.getFieldComment()))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(dataFieldDTO.getStandardStatus())) {
            dataFieldDTOList = dataFieldDTOList.stream()
                    .filter(dto -> dto.getStandardStatus().equals(dataFieldDTO.getStandardStatus()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(dataFieldDTO.getCheckFieldIds())) {
            dataFieldDTOList = dataFieldDTOList.stream()
                    .filter(dto -> !dataFieldDTO.getCheckFieldIds().contains(dto.getFieldId()))
                    .collect(Collectors.toList());
        }
        org.springframework.data.domain.Page<DataFieldDTO> page = PageUtil.doPage(dataFieldDTOList, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize()));
        return PageParseUtil.springPage2C7nPage(page);
    }

    @Override
    public Page<DataFieldDTO> addStandardList(DataFieldDTO dataFieldDTO, PageRequest pageRequest) {
        dataFieldDTO.setAddFlag(1);
        //获取当前标准组待添加字段标准,排除已有的字段标准
        return PageHelper.doPage(pageRequest, () -> dataFieldMapper.list(dataFieldDTO));
    }

    private List<StandardTeam> getSubStandardTeam(Long standardTeamId) {
        List<StandardTeam> subTeamList = standardTeamRepository.select(StandardTeam.builder().parentTeamId(standardTeamId).build());
        List<StandardTeam> subStandardTeamList = new ArrayList<>(subTeamList);
        if (CollectionUtils.isNotEmpty(subTeamList)) {
            subTeamList.forEach(subTeam -> {
                List<StandardTeam> subStandardTeam = getSubStandardTeam(subTeam.getStandardTeamId());
                subStandardTeamList.addAll(subStandardTeam);
            });
        }
        return subStandardTeamList;
    }

    private List<StandardRelation> convertStandardRelation(StandardTeamDTO standardTeamDTO) {
        List<StandardRelation> standardRelations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(standardTeamDTO.getDataFieldDTOList())) {
            List<DataFieldDTO> dataFieldDTOList = standardTeamDTO.getDataFieldDTOList();
            dataFieldDTOList.forEach(dataFieldDTO -> {
                log.info("字段标准的id：" + dataFieldDTO.getFieldId());
                standardRelations.add(StandardRelation.builder()
                        .fieldStandardId(dataFieldDTO.getFieldId())
                        .standardTeamId(standardTeamDTO.getStandardTeamId())
                        .tenantId(standardTeamDTO.getTenantId())
                        .projectId(standardTeamDTO.getProjectId())
                        .build());
            });
        }
        return standardRelations;
    }
}
