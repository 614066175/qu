package com.hand.hdsp.quality.app.service.impl;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.NameStandardService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.NameStandardStatusEnum;
import com.hand.hdsp.quality.infra.converter.NameStandardConverter;
import com.hand.hdsp.quality.infra.util.DataSecurityUtil;
import com.hand.hdsp.quality.infra.vo.NameStandardDatasourceVO;
import com.hand.hdsp.quality.infra.vo.NameStandardTableVO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final String ERROR_MESSAGE = "table name cannot match rule: %s";


    public NameStandardServiceImpl(NameStandardRepository nameStandardRepository,
                                   NameAimRepository nameAimRepository,
                                   NameAimIncludeRepository nameAimIncludeRepository,
                                   NameAimExcludeRepository nameAimExcludeRepository,
                                   NameExecHisDetailRepository nameExecHisDetailRepository,
                                   NameExecHistoryRepository nameExecHistoryRepository,
                                   NameStandardConverter nameStandardConverter,
                                   DriverSessionService driverSessionService) {
        this.nameStandardRepository = nameStandardRepository;
        this.nameAimRepository = nameAimRepository;
        this.nameAimIncludeRepository = nameAimIncludeRepository;
        this.nameAimExcludeRepository = nameAimExcludeRepository;
        this.nameExecHisDetailRepository = nameExecHisDetailRepository;
        this.nameExecHistoryRepository = nameExecHistoryRepository;
        this.nameStandardConverter = nameStandardConverter;
        this.driverSessionService = driverSessionService;
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
        List<NameStandardDTO> dtoList = nameStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(NameStandard.FIELD_TENANT_ID, nameStandardDTO.getTenantId())
                        .andEqualTo(NameStandard.FIELD_STANDARD_NAME, nameStandardDTO.getStandardName()))
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
            nameExecHisDetailDTOList.forEach(x -> {
                if (!Pattern.matches(nameStandard.getStandardRule(), x.getTableName())) {
                    x.setErrorMessage(String.format(ERROR_MESSAGE, nameStandard.getStandardRule()));
                    abnormalList.add(x);
                }
            });
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
    public Page<NameStandardDTO> export(NameStandardDTO dto, ExportParam exportParam, PageRequest pageRequest) {
        //命名标准导出 分组编码、标准编码、命名标准名称、命名标准描述、命名标准类型、命名标准规则、是否忽略大小写、责任人电话、责任人邮箱、责任人姓名、责任部门
        //对责任人电话、责任人邮箱、责任人部门进行解密
        Page<NameStandardDTO> page = PageHelper.doPageAndSort(pageRequest, () -> nameStandardRepository.list(dto));
        decrypt(page);
        return page;
    }

    private void decrypt(List<NameStandardDTO> list) {
        if (DataSecurityHelper.isTenantOpen( ) && CollectionUtils.isNotEmpty(list)) {
            list.forEach(this::decrypt);
        }
    }

    private void decrypt(NameStandardDTO us) {
        Integer apiEncryptFlag = DetailsHelper.getUserDetails().getApiEncryptFlag();
        //判断解密字段当不为空且该租户是加密进行解密
        if (DataSecurityHelper.isTenantOpen( ) && apiEncryptFlag != null && apiEncryptFlag == 1) {
            //判断解密责任人电话
            if (StringUtils.isNotEmpty(us.getChargeTel( ))) {
                us.setChargeTel(DataSecurityUtil.decrypt(us.getChargeTel( )));
            }
            //判断解密责任人邮箱
            if (StringUtils.isNotEmpty(us.getChargeEmail( ))) {
                us.setChargeEmail(DataSecurityUtil.decrypt(us.getChargeEmail( )));
            }
            //判断解密责任人部门
            if (StringUtils.isNotEmpty(us.getChargeDeptName( ))) {
                us.setChargeDeptName(DataSecurityUtil.decrypt(us.getChargeDeptName( )));
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
                        .andEqualTo(NameStandard.FIELD_STANDARD_CODE, nameStandardDTO.getStandardCode()))
                .build());
        if (CollectionUtils.isNotEmpty(dtos)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        dtos = nameStandardRepository.selectDTOByCondition(Condition.builder(NameStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(NameStandard.FIELD_TENANT_ID, nameStandardDTO.getTenantId())
                        .andEqualTo(NameStandard.FIELD_STANDARD_NAME, nameStandardDTO.getStandardName()))
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
            if (!Objects.isNull(nameAimDTO.getExcludeRule())) {
                //获取满足排除规则的表
                List<String> excludeRuleTable = tables.stream().filter(o -> Pattern.matches(nameAimDTO.getExcludeRule(), o))
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

}
