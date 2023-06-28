package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.core.CommonGroupClient;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.NameStandardService;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.NameStandardStatusEnum;
import com.hand.hdsp.quality.infra.converter.NameStandardConverter;
import com.hand.hdsp.quality.infra.export.NameStandardExporter;
import com.hand.hdsp.quality.infra.export.dto.NameStandardExportDTO;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import com.hand.hdsp.quality.infra.util.DataSecurityUtil;
import com.hand.hdsp.quality.infra.vo.NameStandardDatasourceVO;
import com.hand.hdsp.quality.infra.vo.NameStandardTableVO;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.constant.BaseConstant;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>命名标准表应用服务默认实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Service
public class NameStandardServiceImpl implements NameStandardService {

    private final NameStandardRepository nameStandardRepository;
    private final NameAimRepository nameAimRepository;
    private final NameAimIncludeRepository nameAimIncludeRepository;
    private final NameAimExcludeRepository nameAimExcludeRepository;
    private final NameExecHisDetailRepository nameExecHisDetailRepository;
    private final NameExecHistoryRepository nameExecHistoryRepository;
    private final NameStandardConverter nameStandardConverter;
    private final DriverSessionService driverSessionService;
    private final StandardGroupRepository standardGroupRepository;
    private final NameStandardMapper nameStandardMapper;
    private static final String ERROR_MESSAGE = "table name cannot match rule: %s";


    public NameStandardServiceImpl(NameStandardRepository nameStandardRepository,
                                   NameAimRepository nameAimRepository,
                                   NameAimIncludeRepository nameAimIncludeRepository,
                                   NameAimExcludeRepository nameAimExcludeRepository,
                                   NameExecHisDetailRepository nameExecHisDetailRepository,
                                   NameExecHistoryRepository nameExecHistoryRepository,
                                   NameStandardConverter nameStandardConverter,
                                   DriverSessionService driverSessionService,
                                   StandardGroupRepository standardGroupRepository,
                                   NameStandardMapper nameStandardMapper) {
        this.nameStandardRepository = nameStandardRepository;
        this.nameAimRepository = nameAimRepository;
        this.nameAimIncludeRepository = nameAimIncludeRepository;
        this.nameAimExcludeRepository = nameAimExcludeRepository;
        this.nameExecHisDetailRepository = nameExecHisDetailRepository;
        this.nameExecHistoryRepository = nameExecHistoryRepository;
        this.nameStandardConverter = nameStandardConverter;
        this.driverSessionService = driverSessionService;
        this.standardGroupRepository = standardGroupRepository;
        this.nameStandardMapper = nameStandardMapper;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(NameStandardDTO nameStandardDTO) {
        if (Objects.isNull(nameStandardRepository.selectDTOByPrimaryKey(nameStandardDTO))) {
            throw new CommonException(ErrorCode.NAME_STANDARD_NOT_EXIST);
        }
        List<NameExecHistoryDTO> historyList = nameExecHistoryRepository.selectDTO(NameExecHistory.FIELD_STANDARD_ID,
                nameStandardDTO.getStandardId());
        //删除执行历史
        if (CollectionUtils.isNotEmpty(historyList)) {
            List<NameExecHisDetailDTO> detailList = historyList.stream()
                    .map(x -> NameExecHisDetailDTO.builder()
                            .historyId(x.getHistoryId())
                            .tenantId(nameStandardDTO.getTenantId())
                            .build())
                    .collect(Collectors.toList());
            nameExecHisDetailRepository.batchDTODelete(detailList);
            //oracle包含全字段的不能使用batchDTODelete，时间类型作为条件会有问题
            nameExecHistoryRepository.batchDTODeleteByPrimaryKey(historyList);
        }
        List<NameAimDTO> aimDtoList = nameAimRepository.selectDTO(NameAim.FIELD_STANDARD_ID,
                nameStandardDTO.getStandardId());
        //删除落标
        if (CollectionUtils.isNotEmpty(aimDtoList)) {
            //删除落标排除项
            List<NameAimExcludeDTO> excludeDtoList = aimDtoList.stream()
                    .map(x -> NameAimExcludeDTO.builder()
                            .aimId(x.getAimId())
                            .tenantId(nameStandardDTO.getTenantId())
                            .build())
                    .collect(Collectors.toList());
            nameAimExcludeRepository.batchDTODelete(excludeDtoList);

            //删除落标包含项
            List<NameAimIncludeDTO> includeDtoList = aimDtoList.stream()
                    .map(x -> NameAimIncludeDTO.builder()
                            .aimId(x.getAimId())
                            .tenantId(nameStandardDTO.getTenantId())
                            .build())
                    .collect(Collectors.toList());
            nameAimIncludeRepository.batchDTODelete(includeDtoList);
            //删除落标
            nameAimRepository.batchDTODeleteByPrimaryKey(aimDtoList);
        }
        //删除标准
        nameStandardRepository.deleteByPrimaryKey(nameStandardDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bitchRemove(List<NameStandardDTO> standardDtoList) {
        if (CollectionUtils.isEmpty(standardDtoList)) {
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        standardDtoList.forEach(this::remove);
    }

    @Override
    public NameStandardDTO update(NameStandardDTO nameStandardDTO) {
        List<NameStandardDTO> dtoList = nameStandardRepository.selectDTOByCondition(Condition.builder(NameStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(NameStandard.FIELD_TENANT_ID, nameStandardDTO.getTenantId())
                        .andEqualTo(NameStandard.FIELD_STANDARD_NAME, nameStandardDTO.getStandardName())
                        .andEqualTo(NameStandard.FIELD_PROJECT_ID, nameStandardDTO.getProjectId()))
                .build());
        if (dtoList.size() > 1 || (dtoList.size() == 1 && !dtoList.get(0).getStandardCode().equals(nameStandardDTO.getStandardCode()))) {
            throw new CommonException(ErrorCode.NAME_STANDARD_NAME_ALREADY_EXIST);
        }
        nameStandardRepository.updateByDTOPrimaryKeySelective(nameStandardDTO);
        return nameStandardDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void executeStandard(NameStandard nameStandard) {
        if (Objects.isNull(nameStandard)) {
            throw new CommonException(ErrorCode.NAME_STANDARD_NOT_EXIST);
        }
        List<NameAimDTO> nameAimDTOList = nameAimRepository.list(nameStandard.getStandardId());
        NameExecHistoryDTO nameExecHistoryDTO = new NameExecHistoryDTO();
        nameExecHistoryDTO.setExecStartTime(new Date());
        nameExecHistoryDTO.setExecRule(nameStandard.getStandardRule());
        nameExecHistoryDTO.setStandardId(nameStandard.getStandardId());
        nameExecHistoryDTO.setTenantId(nameStandard.getTenantId());
        try {
            //获取目标表
            List<NameExecHisDetailDTO> nameExecHisDetailDTOList = this.getAimTable(nameAimDTOList);
            nameExecHistoryDTO.setCheckedNum((long) nameExecHisDetailDTOList.size());
            List<NameExecHisDetailDTO> abnormalList = new ArrayList<>();
            //判断是否忽略大小写,若忽略则正则、表名都转为大写
            if (BaseConstants.Flag.YES.equals(nameStandard.getIgnoreCaseFlag())) {
                nameExecHisDetailDTOList.forEach(x -> {
                    if (!Pattern.compile(nameStandard.getStandardRule().toUpperCase()).matcher(x.getTableName().toUpperCase()).find()) {
                        x.setErrorMessage(String.format(ERROR_MESSAGE, nameStandard.getStandardRule()));
                        abnormalList.add(x);
                    }
                });
            } else {
                nameExecHisDetailDTOList.forEach(x -> {
                    if (!Pattern.compile(nameStandard.getStandardRule()).matcher(x.getTableName()).find()) {
                        x.setErrorMessage(String.format(ERROR_MESSAGE, nameStandard.getStandardRule()));
                        abnormalList.add(x);
                    }
                });
            }
            nameExecHistoryDTO.setAbnormalNum((long) abnormalList.size());
            nameExecHistoryDTO.setExecEndTime(new Date());
            nameExecHistoryDTO.setExecStatus(NameStandardStatusEnum.SUCCESS.getStatusCode());
            nameExecHistoryRepository.insertDTOSelective(nameExecHistoryDTO);
            abnormalList.forEach(x -> x.setHistoryId(nameExecHistoryDTO.getHistoryId()));
            nameExecHisDetailRepository.batchInsertDTOSelective(abnormalList);

            nameStandard.setLatestCheckedStatus(NameStandardStatusEnum.SUCCESS.getStatusCode());
            nameStandard.setLatestAbnormalNum((long) abnormalList.size());
            nameStandardRepository.updateOptional(nameStandard, NameStandard.FIELD_LATEST_CHECKED_STATUS,
                    NameStandard.FIELD_LATEST_ABNORMAL_NUM);

        } catch (Exception e) {
            nameStandard.setLatestCheckedStatus(NameStandardStatusEnum.FAILED.getStatusCode());
            nameStandardRepository.updateOptional(nameStandard, NameStandard.FIELD_LATEST_CHECKED_STATUS);
            nameExecHistoryDTO.setExecStatus(NameStandardStatusEnum.FAILED.getStatusCode());
            nameExecHistoryDTO.setErrorMessage(e.toString());
            nameExecHistoryRepository.insertDTOSelective(nameExecHistoryDTO);
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchExecuteStandard(List<Long> standardIdList) {
        if (CollectionUtils.isEmpty(standardIdList)) {
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        //将所有标准都改为运行中状态，不符合标准数量设置为-1
        List<NameStandardDTO> nameStandardDTOList = nameStandardRepository.selectDTOByIds(standardIdList);
        nameStandardDTOList.forEach(x -> {
            x.setLatestCheckedStatus(NameStandardStatusEnum.RUNNING.getStatusCode());
            x.setLatestAbnormalNum(-1L);
        });
        List<NameStandard> nameStandards = nameStandardConverter.dtoListToEntityList(nameStandardDTOList);
        nameStandardRepository.batchUpdateOptional(nameStandards, NameStandard.FIELD_LATEST_ABNORMAL_NUM,
                NameStandard.FIELD_LATEST_CHECKED_STATUS);
        nameStandards.forEach(this::executeStandard);
    }

    @Override
    @ProcessLovValue(targetField = "nameStandardDTOList")
    public List<NameStandardExportDTO> export(NameStandardDTO dto, ExportParam exportParam) {
        return ApplicationContextHelper.getContext().getBean(NameStandardExporter.class).export(dto);
    }

    private void handleNameStandardGroupDto(NameStandardGroupDTO nameStandardGroupDto, List<NameStandardDTO> nameStandards, StandardGroupDTO standardGroupDTO, List<NameStandardGroupDTO> nameStandardGroupDTOList, int level) {
        nameStandardGroupDto.setNameStandardDTOList(nameStandards);
        if (ObjectUtils.isNotEmpty(standardGroupDTO.getParentGroupId())) {
            StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByPrimaryKey(standardGroupDTO.getParentGroupId());
            nameStandardGroupDto.setParentGroupCode(groupDTO.getGroupCode());
        }
        nameStandardGroupDTOList.add(nameStandardGroupDto);
        if (ObjectUtils.isNotEmpty(standardGroupDTO.getParentGroupId())) {
            findParentGroups(standardGroupDTO.getParentGroupId(), nameStandardGroupDTOList, level);
        }
    }

    private void findSortedChildGroups(NameStandardGroupDTO parentNameStandardGroupDTO, int level, List<NameStandardGroupDTO> nameStandardGroupDTOList) {
        level++;
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, parentNameStandardGroupDTO.getGroupId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(standardGroupDTO -> {
                NameStandardGroupDTO nameStandardGroupDTO = new NameStandardGroupDTO();
                BeanUtils.copyProperties(standardGroupDTO, nameStandardGroupDTO);
                nameStandardGroupDTO.setGroupLevel(finalLevel);
                //子目录数据标准列表
                List<NameStandardDTO> nameStandardDTOList = nameStandardMapper.list(NameStandardDTO.builder().groupArrays(new Long[]{nameStandardGroupDTO.getGroupId()}).build());
                if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(nameStandardDTOList)) {
                    nameStandardDTOList.forEach(nameStandardDTO -> {
                        if (StringUtils.isNotEmpty(nameStandardDTO.getChargeName())) {
                            nameStandardDTO.setChargeName(DataSecurityHelper.decrypt(nameStandardDTO.getChargeName()));
                        }
                        if (StringUtils.isNotEmpty(nameStandardDTO.getChargeTel())) {
                            nameStandardDTO.setChargeTel(DataSecurityHelper.decrypt(nameStandardDTO.getChargeTel()));
                        }
                        if (StringUtils.isNotEmpty(nameStandardDTO.getChargeEmail())) {
                            nameStandardDTO.setChargeEmail(DataSecurityHelper.decrypt(nameStandardDTO.getChargeEmail()));
                        }
                        if (StringUtils.isNotEmpty(nameStandardDTO.getChargeDeptName())) {
                            nameStandardDTO.setChargeDeptName(DataSecurityHelper.decrypt(nameStandardDTO.getChargeDeptName()));
                        }
                    });
                }
                nameStandardGroupDTO.setNameStandardDTOList(nameStandardDTOList);
                //设置父分组code
                nameStandardGroupDTO.setParentGroupCode(parentNameStandardGroupDTO.getGroupCode());
                nameStandardGroupDTOList.add(nameStandardGroupDTO);
                findSortedChildGroups(nameStandardGroupDTO, finalLevel, nameStandardGroupDTOList);
            });
        }
    }

    private void findParentGroups(Long groupId, List<NameStandardGroupDTO> standardGroups, int level) {
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_ID, groupId))
                .build());
        level++;
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(parentStandardGroupDTO -> {
                NameStandardGroupDTO nameStandardGroupDTO = new NameStandardGroupDTO();
                BeanUtils.copyProperties(parentStandardGroupDTO, nameStandardGroupDTO);
                //获取设置当前分组的父分组编码
                if (ObjectUtils.isNotEmpty(nameStandardGroupDTO.getGroupId())) {
                    StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(nameStandardGroupDTO.getGroupId());
                    nameStandardGroupDTO.setGroupLevel(finalLevel);
                    standardGroups.add(nameStandardGroupDTO);
                    if (ObjectUtils.isNotEmpty(parentGroupDTO.getParentGroupId())) {
                        StandardGroup group = standardGroupRepository.selectByPrimaryKey(parentGroupDTO.getParentGroupId());
                        nameStandardGroupDTO.setParentGroupCode(group.getGroupCode());
                        findParentGroups(parentGroupDTO.getParentGroupId(), standardGroups, finalLevel);
                    }
                }
            });
        }
    }

    private void decrypt(List<NameStandardDTO> list) {
        if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(list)) {
            list.forEach(this::decrypt);
        }
    }

    private void decrypt(NameStandardDTO us) {
        Integer apiEncryptFlag = DetailsHelper.getUserDetails().getApiEncryptFlag();
        //判断解密字段当不为空且该租户是加密进行解密
        if (DataSecurityHelper.isTenantOpen() && apiEncryptFlag != null && apiEncryptFlag == 1) {
            //判断解密责任人电话
            if (StringUtils.isNotEmpty(us.getChargeTel())) {
                us.setChargeTel(DataSecurityUtil.decrypt(us.getChargeTel()));
            }
            //判断解密责任人邮箱
            if (StringUtils.isNotEmpty(us.getChargeEmail())) {
                us.setChargeEmail(DataSecurityUtil.decrypt(us.getChargeEmail()));
            }
            //判断解密责任人部门
            if (StringUtils.isNotEmpty(us.getChargeDeptName())) {
                us.setChargeDeptName(DataSecurityUtil.decrypt(us.getChargeDeptName()));
            }
            //解密员工名
            if (StringUtils.isNotEmpty(us.getChargeName())) {
                us.setChargeName(DataSecurityHelper.decrypt(us.getChargeName()));
            }
        }
    }

    @Override
    public List<NameStandardTableVO> getTables(NameStandardDatasourceVO nameStandardDatasourceVO) {
        if (StringUtils.isEmpty(nameStandardDatasourceVO.getDatasource())
                || CollectionUtils.isEmpty(nameStandardDatasourceVO.getSchemas())) {
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        List<NameStandardTableVO> nameStandardTableVoList = new ArrayList<>(nameStandardDatasourceVO.getSchemas().size());
        DriverSession driverSession = driverSessionService.getDriverSession(DetailsHelper.getUserDetails().getTenantId(),
                nameStandardDatasourceVO.getDatasource());
        nameStandardDatasourceVO.getSchemas().forEach(x -> nameStandardTableVoList
                .add(NameStandardTableVO.builder()
                        .title(x)
                        .id(x)
                        .children(driverSession.tableList(x).stream()
                                .map(o -> NameStandardTableVO.builder()
                                        .id(x + "." + o).title(o)
                                        .build()).collect(Collectors.toList()))
                        .build()));
        return nameStandardTableVoList;
    }

    @Override
    public NameStandardDTO create(NameStandardDTO nameStandardDTO) {
        List<NameStandardDTO> dtos = nameStandardRepository.selectDTOByCondition(Condition.builder(NameStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(NameStandard.FIELD_TENANT_ID, nameStandardDTO.getTenantId())
                        .andEqualTo(NameStandard.FIELD_STANDARD_CODE, nameStandardDTO.getStandardCode())
                        .andEqualTo(NameStandard.FIELD_PROJECT_ID, nameStandardDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtos)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        dtos = nameStandardRepository.selectDTOByCondition(Condition.builder(NameStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(NameStandard.FIELD_TENANT_ID, nameStandardDTO.getTenantId())
                        .andEqualTo(NameStandard.FIELD_STANDARD_NAME, nameStandardDTO.getStandardName())
                        .andEqualTo(NameStandard.FIELD_PROJECT_ID, nameStandardDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtos)) {
            throw new CommonException(ErrorCode.NAME_STANDARD_NAME_ALREADY_EXIST);
        }
        nameStandardRepository.insertDTOSelective(nameStandardDTO);
        return nameStandardDTO;
    }

    /**
     * 获取要校验的表
     *
     * @param nameAimDTO   落标
     * @param aimTableList 接收结果的list
     */
    private void getAimTableFromNameAimDTO(NameAimDTO nameAimDTO, List<NameExecHisDetailDTO> aimTableList) {
        DriverSession driverSession = driverSessionService.getDriverSession(nameAimDTO.getTenantId(), nameAimDTO.getDatasourceCode());
        nameAimDTO.getNameAimIncludeDTOList().forEach(x -> {
            List<String> tables = driverSession.tableList(x.getSchemaName());
            if (CollectionUtils.isEmpty(tables)) {
                throw new CommonException("invalid schema:{0}/{1}", nameAimDTO.getDatasourceCode(), x.getSchemaName());
            }
            if (StringUtils.isNotEmpty(nameAimDTO.getExcludeRule())) {
                List<String> excludeRuleTable = tables.stream().filter(o -> Pattern.compile(nameAimDTO.getExcludeRule()).matcher(o).find())
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(excludeRuleTable)) {
                    //去除满足排除规则的表
                    tables.removeAll(excludeRuleTable);
                }
            }
            List<String> excludeTables = nameAimDTO.getNameAimExcludeDTOList().stream()
                    .filter(o -> o.getSchemaName().equals(x.getSchemaName()))
                    .map(NameAimExcludeDTO::getTableName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(excludeTables)) {
                tables.removeAll(excludeTables);
            }
            aimTableList.addAll(tables.stream()
                    .map(o -> NameExecHisDetailDTO.builder()
                            .tenantId(nameAimDTO.getTenantId())
                            .tableName(o)
                            .schemaName(x.getSchemaName())
                            .sourcePath(nameAimDTO.getDatasourceCode() + "/" + x.getSchemaName() + "/" + o)
                            .build())
                    .collect(Collectors.toList())
            );
        });
    }

    private List<NameExecHisDetailDTO> getAimTable(List<NameAimDTO> nameAimDTOList) {
        List<NameExecHisDetailDTO> aimTableList = new ArrayList<>();
        nameAimDTOList.forEach(x -> this.getAimTableFromNameAimDTO(x, aimTableList));
        return aimTableList;
    }

    @Override
    public Page<NameStandardDTO> pageNameStandards(NameStandardDTO nameStandardDTO, PageRequest pageRequest) {
        //分组查询时同时查询当前分组和当前分组子分组的数据标准
        Long groupId = nameStandardDTO.getGroupId();
        if (ObjectUtils.isNotEmpty(groupId)) {
//            List<StandardGroupDTO> standardGroups = new ArrayList<>();
//            //查询子分组
//            findChildGroups(groupId, standardGroups);
//            //添加当前分组
//            standardGroups.add(StandardGroupDTO.builder().groupId(groupId).build());
//            Long[] groupIds = standardGroups.stream().map(StandardGroupDTO::getGroupId).toArray(Long[]::new);
//            nameStandardDTO.setGroupArrays(groupIds);
            CommonGroupRepository commonGroupRepository = ApplicationContextHelper.getContext().getBean(CommonGroupRepository.class);
            CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(groupId);
            CommonGroupClient commonGroupClient = ApplicationContextHelper.getContext().getBean(CommonGroupClient.class);
            List<CommonGroup> subGroup = commonGroupClient.getSubGroup(commonGroup);
            subGroup.add(commonGroup);
            nameStandardDTO.setGroupArrays(subGroup.stream().map(CommonGroup::getGroupId).toArray(Long[]::new));
        }
        return PageHelper.doPageAndSort(pageRequest, () -> nameStandardRepository.list(nameStandardDTO));
    }

    private void findChildGroups(Long groupId, List<StandardGroupDTO> standardGroups) {
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, groupId))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            standardGroups.addAll(standardGroupDTOList);
            standardGroupDTOList.forEach(standardGroupDTO -> findChildGroups(standardGroupDTO.getGroupId(), standardGroups));
        }
    }
}
