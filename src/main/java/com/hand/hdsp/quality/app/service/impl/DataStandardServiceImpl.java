package com.hand.hdsp.quality.app.service.impl;

import java.util.List;
import java.util.Objects;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.meta.Column;
import org.hzero.starter.driver.core.infra.meta.Table;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:40
 * @since 1.0
 */
@Service
public class DataStandardServiceImpl implements DataStandardService {

    private final DataStandardRepository dataStandardRepository;

    private final DataStandardVersionRepository dataStandardVersionRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final DataStandardMapper dataStandardMapper;

    private final ExtraVersionRepository extraVersionRepository;

    private final StandardAimRepository standardAimRepository;

    private final DriverSessionService driverSessionService;

    private final StandardAimRelationRepository standardAimRelationRepository;

    private final BatchPlanBaseService batchPlanBaseService;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    private final BatchPlanFieldRepository batchPlanFieldRepository;


    public DataStandardServiceImpl(DataStandardRepository dataStandardRepository,
                                   DataStandardVersionRepository dataStandardVersionRepository,
                                   StandardExtraRepository standardExtraRepository,
                                   DataStandardMapper dataStandardMapper,
                                   ExtraVersionRepository extraVersionRepository,
                                   StandardAimRepository standardAimRepository, DriverSessionService driverSessionService, StandardAimRelationRepository standardAimRelationRepository, BatchPlanBaseService batchPlanBaseService, BatchPlanBaseRepository batchPlanBaseRepository, BatchPlanFieldRepository batchPlanFieldRepository) {
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardVersionRepository = dataStandardVersionRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.extraVersionRepository = extraVersionRepository;
        this.standardAimRepository = standardAimRepository;
        this.driverSessionService = driverSessionService;
        this.standardAimRelationRepository = standardAimRelationRepository;
        this.batchPlanBaseService = batchPlanBaseService;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DataStandardDTO dataStandardDTO) {
        List<DataStandardDTO> dataStandardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardDTO.getStandardCode())
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(dataStandardDTOS)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_CODE_EXIST);
        }

        List<DataStandardDTO> standardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardName())
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardDTOList)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_NAME_EXIST);
        }

        dataStandardDTO.setStandardStatus(StandardConstant.CREATE);
        dataStandardRepository.insertDTOSelective(dataStandardDTO);

        List<StandardExtraDTO> standardExtraDTOList = dataStandardDTO.getStandardExtraDTOList();
        if (CollectionUtils.isNotEmpty(standardExtraDTOList)) {
            standardExtraDTOList.forEach(s -> {
                StandardExtraDTO extraDTO = StandardExtraDTO.builder()
                        .standardId(dataStandardDTO.getStandardId())
                        .extraKey(s.getExtraKey())
                        .extraValue(s.getExtraValue())
                        .standardType("DATA")
                        .tenantId(dataStandardDTO.getTenantId())
                        .build();
                standardExtraRepository.insertDTOSelective(extraDTO);
            });
        }
    }

    @Override
    public DataStandardDTO detail(Long tenantId, Long standardId) {
        List<DataStandardDTO> dataStandardDTOList = dataStandardMapper.list(DataStandardDTO
                .builder()
                .standardId(standardId)
                .build());
        if (CollectionUtils.isEmpty(dataStandardDTOList)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_NOT_EXIST);
        }
        DataStandardDTO dataStandardDTO = dataStandardDTOList.get(0);
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, standardId)
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "DATA")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
            dataStandardDTO.setStandardExtraDTOList(standardExtraDTOS);
        }
        return dataStandardDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataStandardDTO dataStandardDTO) {
        if (StandardConstant.ONLINE.equals(dataStandardDTO.getStandardStatus())
                || StandardConstant.OFFLINE_APPROVING.equals(dataStandardDTO.getStandardStatus())) {
            throw new CommonException(ErrorCode.DATA_STANDARD_CAN_NOT_DELETE);
        }
        //暂未做申请审核 todo 删除申请头表行表

        dataStandardRepository.deleteDTO(dataStandardDTO);
        //删除版本表数据
        List<DataStandardVersionDTO> dataStandardVersionDTOS = dataStandardVersionRepository.selectDTOByCondition(Condition.builder(DataStandardVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandardVersion.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(DataStandardVersion.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        dataStandardVersionRepository.batchDTODelete(dataStandardVersionDTOS);
        //删除额外信息表数据
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "DATA")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        standardExtraRepository.batchDTODelete(standardExtraDTOS);
        //删除额外信息历史表数据
        List<ExtraVersionDTO> extraVersionDTOS = extraVersionRepository.selectDTOByCondition(Condition.builder(ExtraVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_TYPE, "DATA")
                        .andEqualTo(ExtraVersion.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        extraVersionRepository.batchDTODelete(extraVersionDTOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(DataStandardDTO dataStandardDTO) {
        DataStandardDTO oldDataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(dataStandardDTO.getStandardId());
        if (Objects.isNull(oldDataStandardDTO)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_NOT_EXIST);
        }
        oldDataStandardDTO.setStandardStatus(dataStandardDTO.getStandardStatus());
        if (StandardConstant.ONLINE_APPROVING.equals(dataStandardDTO.getStandardStatus())) {
            doApprove(oldDataStandardDTO);
        }
        if (StandardConstant.OFFLINE_APPROVING.equals(dataStandardDTO.getStandardStatus())) {
            doApprove(oldDataStandardDTO);
        }
        dataStandardRepository.updateByDTOPrimaryKey(oldDataStandardDTO);
    }


    /**
     * 实现申请动作
     *
     * @param oldDataStandardDTO DataStandardDTO
     */
    private void doApprove(DataStandardDTO oldDataStandardDTO) {
//        //创建申请头
//        ApprovalHeaderDTO headerDTO = ApprovalHeaderDTO.builder()
//                .resourceName(oldDataStandardDTO.getStandardName())
//                .resourceDesc(oldDataStandardDTO.getStandardDesc())
//                .itemType(DATA_STANDARD)
//                .operation(oldDataStandardDTO.getStandardStatus())
//                .applyId(oldDataStandardDTO.getChargeId())
//                .tenantId(oldDataStandardDTO.getTenantId())
//                .build();
//        assetFeign.create(oldDataStandardDTO.getTenantId(),headerDTO);
//        ApprovalHeaderDTO approvalHeaderDTO = assetFeign.getByUnique(oldDataStandardDTO.getTenantId(), headerDTO).getBody();
//        if(Objects.isNull(approvalHeaderDTO)){
//            throw new CommonException("hdsp.xsta.err.approval_header_not_exist");
//        }
//        //创建申请行
//        ApprovalLineDTO approvalLineDTO = ApprovalLineDTO.builder()
//                .approvalId(approvalHeaderDTO.getApprovalId())
//                .operation(StandardConstant.APPROVING)
//                .tenantId(approvalHeaderDTO.getTenantId())
//                .build();
//        assetFeign.create(approvalLineDTO.getTenantId(),approvalLineDTO);
    }

    @Override
    public Page<DataStandardDTO> list(PageRequest pageRequest, DataStandardDTO dataStandardDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataStandardMapper.list(dataStandardDTO));
    }

    @Override
    public void update(DataStandardDTO dataStandardDTO) {
        DataStandardDTO oldDataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(dataStandardDTO.getStandardId());
        dataStandardDTO.setObjectVersionNumber(oldDataStandardDTO.getObjectVersionNumber());
        dataStandardRepository.updateByDTOPrimaryKey(dataStandardDTO);
        //把附加信息表对应数据先清空
        List<StandardExtraDTO> oldStandardExtraDTOS = standardExtraRepository
                .selectDTOByCondition(Condition.builder(StandardExtra.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                                .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "DATA")
                                .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                        .build());
        if (CollectionUtils.isNotEmpty(oldStandardExtraDTOS)) {
            standardExtraRepository.batchDTODelete(oldStandardExtraDTOS);
        }
        //把新的数据插入附加信息表
        List<StandardExtraDTO> newStandardExtraDTOList = dataStandardDTO.getStandardExtraDTOList();
        if (CollectionUtils.isNotEmpty(newStandardExtraDTOList)) {
            newStandardExtraDTOList.forEach(s -> {
                //新增
                StandardExtraDTO standardExtraDTO = StandardExtraDTO.builder()
                        .standardId(dataStandardDTO.getStandardId())
                        .standardType("DATA")
                        .extraKey(s.getExtraKey())
                        .extraValue(s.getExtraValue())
                        .tenantId(dataStandardDTO.getTenantId())
                        .build();
                standardExtraRepository.insertDTOSelective(standardExtraDTO);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishOrOff(DataStandardDTO dataStandardDTO) {
        DataStandardDTO dto = dataStandardRepository.selectDTOByPrimaryKey(dataStandardDTO.getStandardId());
        if (Objects.isNull(dto)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_NOT_EXIST);
        }
        dataStandardDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
        dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);
        //判断数据标准状态,如果是发布上线状态，则存版本表
        if (StandardConstant.ONLINE.equals(dataStandardDTO.getStandardStatus())) {
            Long lastVersion = 1L;
            List<DataStandardVersionDTO> dataStandardVersionDTOS = dataStandardVersionRepository.selectDTOByCondition(Condition.builder(DataStandardVersion.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(DataStandardVersion.FIELD_STANDARD_ID, dataStandardDTO.getStandardId()))
                    .orderByDesc(DataStandardVersion.FIELD_VERSION_NUMBER).build());
            DataStandardVersionDTO dataStandardVersionDTO = new DataStandardVersionDTO();
            //不为空则取最新版本
            if (CollectionUtils.isNotEmpty(dataStandardVersionDTOS)) {
                lastVersion = dataStandardVersionDTOS.get(0).getVersionNumber() + 1;
            }
            //存入版本表
            BeanUtils.copyProperties(dataStandardDTO, dataStandardVersionDTO);
            dataStandardVersionDTO.setVersionNumber(lastVersion);
            dataStandardVersionRepository.insertDTOSelective(dataStandardVersionDTO);
            //存附加信息版本表
            List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                            .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "DATA")
                            .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                    .build());
            ExtraVersionDTO extraVersionDTO = new ExtraVersionDTO();
            if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
                //存附件信息版本表
                for (StandardExtraDTO s : standardExtraDTOS) {
                    BeanUtils.copyProperties(s, extraVersionDTO);
                    extraVersionDTO.setVersionNumber(lastVersion);
                    extraVersionRepository.insertDTOSelective(extraVersionDTO);
                }
            }
        }
    }

    @Override
    public void aim(StandardAimDTO standardAimDTO) {
        List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardAim.FIELD_STANDARD_ID, standardAimDTO.getStandardId())
                        .andEqualTo(StandardAim.FIELD_DATASOURCE_TYPE, standardAimDTO.getDatasourceType())
                        .andEqualTo(StandardAim.FIELD_SCHEMA_NAME, standardAimDTO.getSchemaName())
                        .andEqualTo(StandardAim.FIELD_DATASOURCE_CODE, standardAimDTO.getDatasourceCode())
                        .andEqualTo(StandardAim.FIELD_FIELD_NAME, standardAimDTO.getFieldName()))
                .build());
        if(CollectionUtils.isNotEmpty(standardAimDTOS)){
            throw new CommonException(ErrorCode.STANDARD_AIM_EXIST);
        }
        DriverSession driverSession = driverSessionService.getDriverSession(standardAimDTO.getTenantId(), standardAimDTO.getDatasourceCode());
        //字段注释
        List<Column> columns = driverSession.columnMetaData(standardAimDTO.getSchemaName(), standardAimDTO.getTableName());
        if(CollectionUtils.isNotEmpty(columns)){
            columns.forEach(column -> {
                if(column.getColumnName().equals(standardAimDTO.getFieldName())){
                    standardAimDTO.setFieldDesc(column.getRemarks());
                }
            });
        }
        //表注释
        List<Table> tables = driverSession.tablesNameAndDesc(standardAimDTO.getSchemaName(), standardAimDTO.getTableName());
        if(CollectionUtils.isNotEmpty(tables)){
            tables.forEach(table -> {
                if(table.getTableName().equals(standardAimDTO.getTableName())){
                    standardAimDTO.setTableDesc(table.getRemarks());
                }
            });
        }
        //存入落标表
        standardAimRepository.insertDTOSelective(standardAimDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRelatePlan(List<StandardAimDTO> standardAimDTOList) {
        if(CollectionUtils.isNotEmpty(standardAimDTOList)){
            standardAimDTOList.forEach(standardAimDTO -> {
                //在该评估方案下生成base
                BatchPlanBaseDTO batchPlanBaseDTO = BatchPlanBaseDTO.builder()
                        .datasourceCode(standardAimDTO.getDatasourceCode())
                        .datasourceId(standardAimDTO.getDatasourceId())
                        .datasourceSchema(standardAimDTO.getSchemaName())
                        .planId(standardAimDTO.getPlanId())
                        .sqlType("TABLE")
                        .objectName(standardAimDTO.getTableName())
                        .incrementStrategy("NONE")
                        .tenantId(standardAimDTO.getTenantId())
                        .build();
                batchPlanBaseRepository.insertDTOSelective(batchPlanBaseDTO);


                //根据数据标准在base下生成字段规则头batch_plan_field
                DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(standardAimDTO.getStandardId());
                BatchPlanFieldDTO batchPlanFieldDTO = BatchPlanFieldDTO.builder()
                        .planBaseId(batchPlanBaseDTO.getPlanBaseId())
                        .ruleCode(dataStandardDTO.getStandardCode())
                        .ruleName(dataStandardDTO.getStandardName())
                        .ruleDesc(dataStandardDTO.getStandardDesc())
                        .checkType("STANDARD")
                        .weight(5L)
                        .build();
                batchPlanFieldRepository.insertDTOSelective(batchPlanFieldDTO);

                //根据数据标准生成具体的校验项batch_plan_field_line


                //根据具体落标存入落标关系表
                StandardAimRelationDTO standardAimRelationDTO = StandardAimRelationDTO.builder()
                        .aimId(standardAimDTO.getAimId())
                        .aimType("AIM")
                        .planId(standardAimDTO.getPlanId())
                        .planBaseId(batchPlanBaseDTO.getPlanBaseId())
                        .planRuleId(batchPlanFieldDTO.getPlanRuleId())
                        .build();
                standardAimRelationRepository.insertDTOSelective(standardAimRelationDTO);
            });
        }
    }
}
