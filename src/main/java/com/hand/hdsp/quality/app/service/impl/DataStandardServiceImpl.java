package com.hand.hdsp.quality.app.service.impl;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.*;
import static com.hand.hdsp.quality.infra.constant.PlanConstant.CheckType.STANDARD;
import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareWay.RANGE;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.LengthType.FIXED;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.app.service.StandardAimService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardConstant.AimType;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.feign.AssetFeign;
import com.hand.hdsp.quality.infra.feign.WorkFlowFeign;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import com.hand.hdsp.quality.infra.util.DataLengthHandler;
import com.hand.hdsp.quality.infra.util.DataPatternHandler;
import com.hand.hdsp.quality.infra.util.StandardHandler;
import com.hand.hdsp.quality.infra.util.ValueRangeHandler;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.meta.Table;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public static final String DEFAULT_START = "0";

    public static final Long DEFAULT_WEIGHT = 5L;

    public static final Long DEFAULT_VERSION = 1L;

    private static final String DATA_STANDARD = "DATA_STANDARD";

    private final DataStandardRepository dataStandardRepository;

    private final DataStandardVersionRepository dataStandardVersionRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final DataStandardMapper dataStandardMapper;

    private final ExtraVersionRepository extraVersionRepository;

    private final StandardAimRepository standardAimRepository;


    private final StandardAimRelationRepository standardAimRelationRepository;

    private final BatchPlanBaseRepository batchPlanBaseRepository;

    private final BatchPlanFieldRepository batchPlanFieldRepository;

    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;

    private final BatchPlanFieldConRepository batchPlanFieldConRepository;

    private final StandardAimService standardAimService;

    private final DataPatternHandler dataPatternHandler;

    private final DataLengthHandler dataLengthHandler;

    private final ValueRangeHandler valueRangeHandler;


    private final DriverSessionService driverSessionService;

    private final StandardGroupRepository standardGroupRepository;

    @Autowired
    private WorkflowClient workflowClient;



    @Autowired
    private List<StandardHandler> handlers;

    @Value("${hdsp.workflow.enabled:false}")
    private boolean enableWorkFlow;

    @Resource
    private AssetFeign assetFeign;

    public DataStandardServiceImpl(DataStandardRepository dataStandardRepository,
                                   DataStandardVersionRepository dataStandardVersionRepository,
                                   StandardExtraRepository standardExtraRepository,
                                   DataStandardMapper dataStandardMapper,
                                   ExtraVersionRepository extraVersionRepository,
                                   StandardAimRepository standardAimRepository,
                                   StandardAimRelationRepository standardAimRelationRepository,
                                   BatchPlanBaseRepository batchPlanBaseRepository,
                                   BatchPlanFieldRepository batchPlanFieldRepository,
                                   BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                   BatchPlanFieldConRepository batchPlanFieldConRepository,
                                   StandardAimService standardAimService,
                                   DataPatternHandler dataPatternHandler,
                                   DataLengthHandler dataLengthHandler,
                                   ValueRangeHandler valueRangeHandler,
                                   DriverSessionService driverSessionService, StandardGroupRepository standardGroupRepository) {
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardVersionRepository = dataStandardVersionRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.extraVersionRepository = extraVersionRepository;
        this.standardAimRepository = standardAimRepository;
        this.standardAimRelationRepository = standardAimRelationRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.batchPlanFieldConRepository = batchPlanFieldConRepository;
        this.standardAimService = standardAimService;
        this.dataPatternHandler = dataPatternHandler;
        this.dataLengthHandler = dataLengthHandler;
        this.valueRangeHandler = valueRangeHandler;
        this.driverSessionService = driverSessionService;
        this.standardGroupRepository = standardGroupRepository;
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
        convertToDataLength(dataStandardDTO);
        dataStandardDTO.setStandardStatus(CREATE);
        dataStandardRepository.insertDTOSelective(dataStandardDTO);

        List<StandardExtraDTO> standardExtraDTOList = dataStandardDTO.getStandardExtraDTOList();
        if (CollectionUtils.isNotEmpty(standardExtraDTOList)) {
            standardExtraDTOList.forEach(s -> {
                StandardExtraDTO extraDTO = StandardExtraDTO.builder()
                        .standardId(dataStandardDTO.getStandardId())
                        .extraKey(s.getExtraKey())
                        .extraValue(s.getExtraValue())
                        .standardType(DATA)
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
        //判断当前租户是否启用安全加密
        if(dataStandardMapper.isEncrypt(tenantId)==1){
            //解密邮箱，电话
            if (Strings.isNotEmpty(dataStandardDTO.getChargeTel())) {
                dataStandardDTO.setChargeTel(DataSecurityHelper.decrypt(dataStandardDTO.getChargeTel()));
            }
            if (Strings.isNotEmpty(dataStandardDTO.getChargeEmail())) {
                dataStandardDTO.setChargeEmail(DataSecurityHelper.decrypt(dataStandardDTO.getChargeEmail()));
            }
            if (Strings.isNotEmpty(dataStandardDTO.getChargeDeptName())) {
                dataStandardDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataStandardDTO.getChargeDeptName()));
            }
        }

        convertToDataLengthList(dataStandardDTO);
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, standardId)
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        dataStandardDTO.setStandardExtraDTOList(standardExtraDTOS);
        return dataStandardDTO;
    }

    private void convertToDataLengthList(DataStandardDTO dataStandardDTO) {
        //对数据长度进行处理
        if (Strings.isNotEmpty(dataStandardDTO.getDataLength())) {
            List<String> dataLength = Arrays.asList(dataStandardDTO.getDataLength().split(","));
            List<Long> dataLengthList = dataLength.stream().map(Long::parseLong).collect(Collectors.toList());
            dataStandardDTO.setDataLengthList(dataLengthList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataStandardDTO dataStandardDTO) {
        if (ONLINE.equals(dataStandardDTO.getStandardStatus())
                || OFFLINE_APPROVING.equals(dataStandardDTO.getStandardStatus())) {
            throw new CommonException(ErrorCode.DATA_STANDARD_CAN_NOT_DELETE);
        }
        //暂未做申请审核 todo 删除申请头表行表

        dataStandardRepository.deleteByPrimaryKey(dataStandardDTO.getStandardId());
        //删除版本表数据
        List<DataStandardVersionDTO> dataStandardVersionDTOS = dataStandardVersionRepository.selectDTOByCondition(Condition.builder(DataStandardVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandardVersion.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(DataStandardVersion.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        dataStandardVersionRepository.batchDTODeleteByPrimaryKey(dataStandardVersionDTOS);
        //删除额外信息表数据
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        standardExtraRepository.batchDTODeleteByPrimaryKey(standardExtraDTOS);
        //删除额外信息历史表数据
        List<ExtraVersionDTO> extraVersionDTOS = extraVersionRepository.selectDTOByCondition(Condition.builder(ExtraVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(ExtraVersion.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        extraVersionRepository.batchDTODeleteByPrimaryKey(extraVersionDTOS);
        //删除数据标准的标准落标以及关系表
        List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardAim.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardAim.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        standardAimService.batchDelete(standardAimDTOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(DataStandardDTO dataStandardDTO) {
        DataStandardDTO oldDataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(dataStandardDTO.getStandardId());
        if (Objects.isNull(oldDataStandardDTO)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_NOT_EXIST);
        }
        oldDataStandardDTO.setStandardStatus(dataStandardDTO.getStandardStatus());
        if (ONLINE_APPROVING.equals(dataStandardDTO.getStandardStatus())) {
            doApprove(oldDataStandardDTO);
        }
        if (OFFLINE_APPROVING.equals(dataStandardDTO.getStandardStatus())) {
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
        convertToDataLength(dataStandardDTO);
        dataStandardRepository.updateByDTOPrimaryKey(dataStandardDTO);
    }

    private void convertToDataLength(DataStandardDTO dataStandardDTO) {
        //对数据长度进行处理
        List<Long> dataLengthList = dataStandardDTO.getDataLengthList();
        if (CollectionUtils.isNotEmpty(dataLengthList)) {
            if (1 == dataLengthList.size()) {
                dataStandardDTO.setDataLength(String.valueOf(dataLengthList.get(0)));
            }
            if (2 == dataLengthList.size()) {
                dataStandardDTO.setDataLength(String.format("%s,%s", dataLengthList.get(0), dataLengthList.get(1)));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishOrOff(DataStandardDTO dataStandardDTO) {
        if(enableWorkFlow){
            //开启工作流
            //根据上下线状态开启不同的工作流实例
            if(ONLINE.equals(dataStandardDTO.getStandardStatus())){
                this.workflowing(dataStandardDTO.getTenantId(),dataStandardDTO.getStandardCode(),ONLINE_APPROVING);
                this.startWorkFlow(WorkFlowConstant.DataStandard.ONLINE_WORKFLOW_KEY,dataStandardDTO);
            }
            if(OFFLINE.equals(dataStandardDTO.getStandardStatus())){
                this.workflowing(dataStandardDTO.getTenantId(),dataStandardDTO.getStandardCode(),OFFLINE_APPROVING);
                this.startWorkFlow(WorkFlowConstant.DataStandard.OFFLINE_WORKFLOW_KEY,dataStandardDTO);
            }
        }else{
            //通用上线下线
            doPublishOrOff(dataStandardDTO);
        }
    }

    private void doPublishOrOff(DataStandardDTO dataStandardDTO) {
        DataStandardDTO dto = dataStandardRepository.selectDTOByPrimaryKey(dataStandardDTO.getStandardId());
        if (Objects.isNull(dto)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_NOT_EXIST);
        }
        dataStandardDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
        dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);
        //判断数据标准状态,如果是发布上线状态，则存版本表
        if (ONLINE.equals(dataStandardDTO.getStandardStatus())) {
            //存版本表
            doVersion(dataStandardDTO);
            //1.数据标准没有关联评估方案，直接发布，不做处理
            //2.数据标准关联了评估方案，第一次发布，则落标到数据质量生成规则
            //3.数据标准关联了评估方案，不是第一发布，则更新落标到数据质量的规则

            //查看此标准落标表的情况
            List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardAim.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                            .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, DATA)
                            .andEqualTo(StandardAim.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                    .build());
            if (CollectionUtils.isNotEmpty(standardAimDTOS)) {
                //过滤出关联了评估方案的落标
                List<StandardAimDTO> aimDTOS = standardAimDTOS.stream()
                        .filter(s -> Objects.nonNull(s.getPlanId()))
                        .collect(Collectors.toList());
                publishRelatePlan(aimDTOS);
            }
            assetFeign.saveStandardToEs(dataStandardDTO.getTenantId(), dataStandardDTO);
        }
        if (OFFLINE.equals(dataStandardDTO.getStandardStatus())) {
            assetFeign.deleteStandardToEs(dataStandardDTO.getTenantId(), dataStandardDTO);
        }
    }

    /**
     * 发布时处理关联了评估的落标
     *
     * @param aimDTOS List<StandardAimDTO>
     */
    private void publishRelatePlan(List<StandardAimDTO> aimDTOS) {
        if (CollectionUtils.isNotEmpty(aimDTOS)) {
            aimDTOS.forEach(standardAimDTO -> {
                //判断落标是否已经关联到数据质量，如果已关联则更新（删除重建）
                List<StandardAimRelationDTO> standardAimRelationDTOS = standardAimRelationRepository
                        .selectDTOByCondition(Condition.builder(StandardAimRelation.class)
                                .andWhere(Sqls.custom()
                                        .andEqualTo(StandardAimRelation.FIELD_AIM_ID, standardAimDTO.getAimId())
                                        .andEqualTo(StandardAimRelation.FIELD_TENANT_ID, standardAimDTO.getTenantId()))
                                .build());
                //已落标关联
                if (CollectionUtils.isNotEmpty(standardAimRelationDTOS)) {
                    StandardAimRelationDTO standardAimRelationDTO = standardAimRelationDTOS.get(0);
                    //查找出数据标准生成的行信息
                    List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOS = batchPlanFieldLineRepository.
                            selectDTOByCondition(Condition.builder(BatchPlanFieldLine.class)
                                    .andWhere(Sqls.custom()
                                            .andEqualTo(BatchPlanFieldLine.FIELD_PLAN_RULE_ID, standardAimRelationDTO.getPlanRuleId())
                                            .andEqualTo(BatchPlanFieldLine.FIELD_TENANT_ID, standardAimRelationDTO.getTenantId()))
                                    .build());
                    //根据行ID删除数据质量字段规则Condition
                    if (CollectionUtils.isNotEmpty(batchPlanFieldLineDTOS)) {
                        batchPlanFieldLineDTOS.forEach(batchPlanFieldLineDTO -> batchPlanFieldConRepository.deleteByPlanLineId(batchPlanFieldLineDTO.getPlanLineId()));
                    }
                    //批量删除line
                    batchPlanFieldLineRepository.batchDTODelete(batchPlanFieldLineDTOS);
                    //删除file_rule
                    batchPlanFieldRepository.deleteByPrimaryKey(standardAimRelationDTO.getPlanRuleId());
                    //删除落标关系表数据
                    standardAimRelationRepository.deleteDTO(standardAimRelationDTO);
                }
                //关联评估方案生成数据质量规则
                doRelatePlan(standardAimDTO);
            });
        }
    }

    private void doVersion(DataStandardDTO dataStandardDTO) {
        Long lastVersion = DEFAULT_VERSION;
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
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
            //存附件信息版本表
            for (StandardExtraDTO s : standardExtraDTOS) {
                ExtraVersionDTO extraVersionDTO = new ExtraVersionDTO();
                BeanUtils.copyProperties(s, extraVersionDTO);
                extraVersionDTO.setVersionNumber(lastVersion);
                extraVersionRepository.insertDTOSelective(extraVersionDTO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aim(Long tenantId, List<StandardAimDTO> standardAimDTOList) {
        if (CollectionUtils.isNotEmpty(standardAimDTOList)) {
            standardAimDTOList.forEach(standardAimDTO -> {
                standardAimDTO.setTenantId(tenantId);
                List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardAim.FIELD_STANDARD_ID, standardAimDTO.getStandardId())
                                .andEqualTo(StandardAim.FIELD_DATASOURCE_TYPE, standardAimDTO.getDatasourceType())
                                .andEqualTo(StandardAim.FIELD_SCHEMA_NAME, standardAimDTO.getSchemaName())
                                .andEqualTo(StandardAim.FIELD_DATASOURCE_CODE, standardAimDTO.getDatasourceCode())
                                .andEqualTo(StandardAim.FIELD_FIELD_NAME, standardAimDTO.getFieldName()))
                        .build());
                if (CollectionUtils.isNotEmpty(standardAimDTOS)) {
                    throw new CommonException(ErrorCode.STANDARD_AIM_EXIST);
                }
                standardAimDTO.setFieldName(String.format("%s(%s)", standardAimDTO.getFieldName(), standardAimDTO.getTypeName()));
                //存入落标表
                standardAimRepository.insertDTOSelective(standardAimDTO);
            });
        }
    }

    /**
     * 批量关联评估方案
     *
     * @param standardAimDTOList List<StandardAimDTO>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRelatePlan(List<StandardAimDTO> standardAimDTOList) {
        //判断数据标准状态，非在线状态，则只存落标表，在数据质量不生成规则
        if (CollectionUtils.isEmpty(standardAimDTOList)) {
            throw new CommonException(ErrorCode.STANDARD_AIM_LIST_IS_EMPTY);
        }
        standardAimDTOList.forEach(standardAimDTO -> {
            //删除原先的落标关系表
            List<StandardAimRelationDTO> standardAimRelationDTOS = standardAimRelationRepository.selectDTOByCondition(Condition.builder(StandardAimRelation.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardAimRelation.FIELD_AIM_ID, standardAimDTO.getAimId())
                            .andEqualTo(StandardAimRelation.FIELD_AIM_TYPE, AimType.AIM)
                            .andEqualTo(StandardAimRelation.FIELD_TENANT_ID, standardAimDTO.getTenantId()))
                    .build());
            standardAimRelationRepository.batchDTODeleteByPrimaryKey(standardAimRelationDTOS);
            DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(standardAimDTO.getStandardId());
            //如果标准为在线状态则去数据质量落标对应规则
            if (StandardStatus.ONLINE.equals(dataStandardDTO.getStandardStatus())) {
                doRelatePlan(standardAimDTO);
            }
            //其余状态只是将评估方案保存下来
            standardAimRepository.updateByDTOPrimaryKeySelective(standardAimDTO);
        });
    }

    @Override
    public List<DataStandardDTO> export(DataStandardDTO dto, ExportParam exportParam, PageRequest pageRequest) {
        return list(pageRequest, dto);
    }

    @Override
    public BatchPlanFieldDTO standardToRule(Long standardId) {
        DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(standardId);
        BatchPlanFieldDTO batchPlanFieldDTO = BatchPlanFieldDTO.builder()
                .ruleCode(dataStandardDTO.getStandardCode())
                .ruleName(dataStandardDTO.getStandardName())
                .ruleDesc(dataStandardDTO.getStandardDesc())
                .checkType(STANDARD)
                .weight(DEFAULT_WEIGHT)
                .build();
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList = new ArrayList<>();
        handlers.forEach(standardHandler -> {
            BatchPlanFieldLineDTO fieldLineDTO = standardHandler.handle(dataStandardDTO);
            if(fieldLineDTO!=null){
                batchPlanFieldLineDTOList.add(fieldLineDTO);
            }
        });
        batchPlanFieldDTO.setBatchPlanFieldLineDTOList(batchPlanFieldLineDTOList);
        return batchPlanFieldDTO;
    }

    @Override
    public void fieldAimStandard(AssetFieldDTO assetFieldDTO) {
        //根据字段删除落标
        List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardAim.FIELD_DATASOURCE_CODE, assetFieldDTO.getDatasourceCode())
                        .andEqualTo(StandardAim.FIELD_SCHEMA_NAME, assetFieldDTO.getDatasourceSchema())
                        .andEqualTo(StandardAim.FIELD_TABLE_NAME, assetFieldDTO.getTableName())
                        .andEqualTo(StandardAim.FIELD_FIELD_NAME, assetFieldDTO.getFieldName()))
                .build());
        standardAimRepository.batchDTODeleteByPrimaryKey(standardAimDTOS);
        //创建新的字段落标
        if (CollectionUtils.isNotEmpty(assetFieldDTO.getStandardIdList())) {
            assetFieldDTO.getStandardIdList().forEach(standardId -> {
                DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(standardId);
                if (Objects.isNull(dataStandardDTO)) {
                    throw new CommonException(ErrorCode.DATA_STANDARD_NOT_EXIST);
                }
                assetFieldDTO.setStandardId(standardId);
                doAim(assetFieldDTO);
            });
        }
    }

    @Override
    public List<DataStandardDTO> standardByField(AssetFieldDTO assetFieldDTO) {
        List<DataStandardDTO> dataStandardDTOList = new ArrayList<>();
        List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardAim.FIELD_DATASOURCE_CODE, assetFieldDTO.getDatasourceCode())
                        .andEqualTo(StandardAim.FIELD_SCHEMA_NAME, assetFieldDTO.getDatasourceSchema())
                        .andEqualTo(StandardAim.FIELD_TABLE_NAME, assetFieldDTO.getTableName())
                        .andEqualTo(StandardAim.FIELD_FIELD_NAME, assetFieldDTO.getFieldName()))
                .build());
        if (CollectionUtils.isNotEmpty(standardAimDTOS)) {
            standardAimDTOS.forEach(standardAimDTO -> {
                DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(standardAimDTO.getStandardId());
                dataStandardDTOList.add(dataStandardDTO);
            });
        }
        return dataStandardDTOList;
    }

    @Override
    public DataStandardDTO assetDetail(Long tenantId, Long standardId) {
        List<DataStandardDTO> dataStandardDTOList = dataStandardMapper.list(DataStandardDTO
                .builder()
                .standardId(standardId)
                .build());
        if (CollectionUtils.isEmpty(dataStandardDTOList)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_NOT_EXIST);
        }
        DataStandardDTO dataStandardDTO = dataStandardDTOList.get(0);
        convertToDataLengthList(dataStandardDTO);
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, standardId)
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, DATA)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        dataStandardDTO.setStandardExtraDTOList(standardExtraDTOS);
        dataStandardDTO.setMetadataName(dataStandardDTO.getStandardName());
        dataStandardDTO.setMetadataType(DATA_STANDARD);
        //查询资源路径
        dataStandardDTO.setNameLevelPath(dataStandardDTO.getGroupName());
        if (Objects.nonNull(dataStandardDTO.getParentGroupId())) {
            while (Objects.nonNull(dataStandardDTO.getParentGroupId())) {
                List<StandardGroup> standardGroups = standardGroupRepository
                        .selectByCondition(Condition.builder(StandardGroup.class)
                                .andWhere(Sqls.custom()
                                        .andEqualTo(StandardGroup.FIELD_GROUP_ID, dataStandardDTO.getParentGroupId())
                                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, tenantId))
                                .build());
                dataStandardDTO.setParentGroupId(standardGroups.get(0).getParentGroupId());
                dataStandardDTO.setNameLevelPath(String.format("%s/%s",
                        standardGroups.get(0).getGroupName(),
                        dataStandardDTO.getNameLevelPath()));
            }
        }
        return dataStandardDTO;
    }

    @Override
    public void startWorkFlow(String workflowKey,DataStandardDTO dataStandardDTO) {
        //使用当前时间戳作为业务主键
        String bussinessKey=String.valueOf(System.currentTimeMillis());
        Map<String, Object> var = new HashMap<>();
        //给流程变量
        var.put("dataStandardCode", dataStandardDTO.getStandardCode());
        //使用自研工作流客户端
        workflowClient.startInstanceByFlowKey(dataStandardDTO.getTenantId(), workflowKey, bussinessKey, "USER",String.valueOf(DetailsHelper.getUserDetails().getUserId()), var);
//        workFlowFeign.startInstanceByFlowKey(dataStandardDTO.getTenantId(), workflowKey, bussinessKey, "USER",String.valueOf(DetailsHelper.getUserDetails().getUserId()), var);
    }

    @Override
    public List<AssigneeUserDTO> findCharger(Long tenantId, String dataStandardCode) {
        List<DataStandardDTO> standardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardCode))
                .build());
        if (CollectionUtils.isNotEmpty(standardDTOS)) {
            Long chargeId = standardDTOS.get(0).getChargeId();
            //查询用户
            return Arrays.asList(dataStandardMapper.selectAssigneeUser(chargeId));
        }else{
            throw new CommonException(ErrorCode.NOT_FIND_VALUE);
        }
    }

    @Override
    public void onlineWorkflowSuccess(Long tenantId, String dataStandardCode) {
        List<DataStandardDTO> standardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardCode))
                .build());
        if (CollectionUtils.isNotEmpty(standardDTOS)) {
            DataStandardDTO dataStandardDTO = standardDTOS.get(0);
            dataStandardDTO.setStandardStatus(ONLINE);
            dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);
            //存版本表
            doVersion(dataStandardDTO);
            //1.数据标准没有关联评估方案，直接发布，不做处理
            //2.数据标准关联了评估方案，第一次发布，则落标到数据质量生成规则
            //3.数据标准关联了评估方案，不是第一发布，则更新落标到数据质量的规则
            //查看此标准落标表的情况
            List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardAim.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                            .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, DATA)
                            .andEqualTo(StandardAim.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                    .build());
            if (CollectionUtils.isNotEmpty(standardAimDTOS)) {
                //过滤出关联了评估方案的落标
                List<StandardAimDTO> aimDTOS = standardAimDTOS.stream()
                        .filter(s -> Objects.nonNull(s.getPlanId()))
                        .collect(Collectors.toList());
                publishRelatePlan(aimDTOS);
            }
            assetFeign.saveStandardToEs(dataStandardDTO.getTenantId(), dataStandardDTO);
        }
    }

    @Override
    public void onlineWorkflowFail(Long tenantId, String dataStandardCode) {
        //上线失败，修改发布审核中状态未离线
        workflowing(tenantId,dataStandardCode,OFFLINE);
    }

    @Override
    public void offlineWorkflowSuccess(Long tenantId, String dataStandardCode) {
        List<DataStandardDTO> standardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardCode))
                .build());
        if (CollectionUtils.isNotEmpty(standardDTOS)) {
            DataStandardDTO dataStandardDTO = standardDTOS.get(0);
            dataStandardDTO.setStandardStatus(OFFLINE);
            dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);
            assetFeign.deleteStandardToEs(dataStandardDTO.getTenantId(), dataStandardDTO);
        }

    }

    @Override
    public void offlineWorkflowFail(Long tenantId, String dataStandardCode) {
        workflowing(tenantId,dataStandardCode,ONLINE);
    }

    @Override
    public void offlineWorkflowing(Long tenantId, String dataStandardCode) {
        workflowing(tenantId,dataStandardCode,OFFLINE_APPROVING);
    }

    @Override
    public void onlineWorkflowing(Long tenantId, String dataStandardCode) {
        workflowing(tenantId,dataStandardCode,ONLINE_APPROVING);
    }


    /**
     * 指定数据标准修改状态，供审批中，审批结束任务状态变更
     * @param tenantId
     * @param dataStandardCode
     * @param status
     */
    private void workflowing(Long tenantId, String dataStandardCode,String status) {
        List<DataStandardDTO> standardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardCode))
                .build());
        if (CollectionUtils.isNotEmpty(standardDTOS)) {
            DataStandardDTO dataStandardDTO = standardDTOS.get(0);
            dataStandardDTO.setStandardStatus(status);
            dataStandardRepository.updateDTOOptional(dataStandardDTO, DataStandard.FIELD_STANDARD_STATUS);
        }
    }

    private void doAim(AssetFieldDTO assetFieldDTO) {
        StandardAimDTO standardAimDTO = StandardAimDTO.builder()
                .standardId(assetFieldDTO.getStandardId())
                .standardType(DATA)
                .fieldName(assetFieldDTO.getFieldName())
                .fieldDesc(assetFieldDTO.getFieldDesc())
                .datasourceId(assetFieldDTO.getDatasourceId())
                .datasourceCode(assetFieldDTO.getDatasourceCode())
                .datasourceType(assetFieldDTO.getDatasourceType())
                .schemaName(assetFieldDTO.getDatasourceSchema())
                .tableName(assetFieldDTO.getTableName())
                .build();
        DriverSession driverSession = driverSessionService.getDriverSession(assetFieldDTO.getTenantId(), assetFieldDTO.getDatasourceCode());
        List<Table> tables = driverSession.tablesNameAndDesc(assetFieldDTO.getDatasourceSchema());
        tables.forEach(table -> {
            if (assetFieldDTO.getTableName().equals(table.getTableName())) {
                standardAimDTO.setTableDesc(table.getRemarks());
            }
        });
        standardAimRepository.insertDTOSelective(standardAimDTO);
    }


    /**
     * 关联评估方案
     *
     * @param standardAimDTO StandardAimDTO
     */
    private void doRelatePlan(StandardAimDTO standardAimDTO) {
        //告警等级集合
        List<WarningLevelDTO> warningLevelDTOList;
        //判断在评估方案下是否已存在相同base
        List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(BatchPlanBase.FIELD_PLAN_ID, standardAimDTO.getPlanId())
                        .andEqualTo(BatchPlanBase.FIELD_DATASOURCE_TYPE, standardAimDTO.getDatasourceType())
                        .andEqualTo(BatchPlanBase.FIELD_DATASOURCE_CODE, standardAimDTO.getDatasourceCode())
                        .andEqualTo(BatchPlanBase.FIELD_DATASOURCE_SCHEMA, standardAimDTO.getSchemaName())
                        .andEqualTo(BatchPlanBase.FIELD_SQL_TYPE, SqlType.TABLE)
                        .andEqualTo(BatchPlanBase.FIELD_OBJECT_NAME, standardAimDTO.getTableName())
                        .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, standardAimDTO.getTenantId()))
                .build());
        BatchPlanBaseDTO batchPlanBaseDTO;
        if (CollectionUtils.isEmpty(batchPlanBaseDTOS)) {
            //没有的话在该评估方案下生成base
            batchPlanBaseDTO = BatchPlanBaseDTO.builder()
                    .datasourceType(standardAimDTO.getDatasourceType())
                    .datasourceCode(standardAimDTO.getDatasourceCode())
                    .datasourceId(standardAimDTO.getDatasourceId())
                    .datasourceSchema(standardAimDTO.getSchemaName())
                    .planId(standardAimDTO.getPlanId())
                    .sqlType(SqlType.TABLE)
                    .objectName(standardAimDTO.getTableName())
                    .incrementStrategy(IncrementStrategy.NONE)
                    .tenantId(standardAimDTO.getTenantId())
                    .build();
            batchPlanBaseRepository.insertDTOSelective(batchPlanBaseDTO);
        } else {
            //使用现有的base
            batchPlanBaseDTO = batchPlanBaseDTOS.get(0);
        }

        //根据数据标准在base下生成字段规则头batch_plan_field
        DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(standardAimDTO.getStandardId());
        List<BatchPlanFieldDTO> batchPlanFieldDTOS = batchPlanFieldRepository.selectDTOByCondition(Condition.builder(BatchPlanField.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(BatchPlanField.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId())
                        .andEqualTo(BatchPlanField.FIELD_RULE_CODE, dataStandardDTO.getStandardCode())
                        .andEqualTo(BatchPlanField.FIELD_RULE_NAME, dataStandardDTO.getStandardName())
                        .andEqualTo(BatchPlanField.FIELD_RULE_DESC, dataStandardDTO.getStandardDesc())
                        .andEqualTo(BatchPlanField.FIELD_CHECK_TYPE, STANDARD))
                .build());
        BatchPlanFieldDTO batchPlanFieldDTO;
        if (CollectionUtils.isEmpty(batchPlanFieldDTOS)) {
            batchPlanFieldDTO = BatchPlanFieldDTO.builder()
                    .planBaseId(batchPlanBaseDTO.getPlanBaseId())
                    .ruleCode(dataStandardDTO.getStandardCode())
                    .ruleName(dataStandardDTO.getStandardName())
                    .ruleDesc(dataStandardDTO.getStandardDesc())
                    .checkType(STANDARD)
                    .weight(DEFAULT_WEIGHT)
                    .build();
            batchPlanFieldRepository.insertDTOSelective(batchPlanFieldDTO);
        } else {
            batchPlanFieldDTO = batchPlanFieldDTOS.get(0);
        }


        //根据数据标准生成具体的校验项batch_plan_field_line
        //数据格式
        if (Strings.isNotEmpty(dataStandardDTO.getDataPattern())) {
            BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                    .checkWay(CheckWay.REGULAR)
                    .checkItem(CheckItem.REGULAR)
                    .planRuleId(batchPlanFieldDTO.getPlanRuleId())
                    .fieldName(standardAimDTO.getFieldName())
                    .regularExpression(dataStandardDTO.getDataPattern())
                    .tenantId(standardAimDTO.getTenantId())
                    .build();
            batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
            //生成每个校验项的配置项
            WarningLevelDTO warningLevelDTO = WarningLevelDTO.builder()
                    .warningLevel(WarningLevel.ORANGE)
                    .compareSymbol(CompareSymbol.EQUAL)
                    .build();
            warningLevelDTOList = Collections.singletonList(warningLevelDTO);
            String warningLevel = JsonUtil.toJson(warningLevelDTOList);
            BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                    .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                    .warningLevel(warningLevel)
                    .build();
            batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
        }
        //数据长度
        if (Strings.isNotEmpty(dataStandardDTO.getDataLength())) {
            BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                    .checkWay(CheckWay.COMMON)
                    .checkItem(CheckItem.DATA_LENGTH)
                    .planRuleId(batchPlanFieldDTO.getPlanRuleId())
                    .fieldName(standardAimDTO.getFieldName())
                    .tenantId(standardAimDTO.getTenantId())
                    .build();
            //固定值
            if (FIXED.equals(dataStandardDTO.getLengthType())) {
                batchPlanFieldLineDTO.setCountType(CountType.FIXED_VALUE);
                batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
                //生成每个校验项的配置项
                WarningLevelDTO warningLevelDTO = WarningLevelDTO.builder()
                        .warningLevel(WarningLevel.ORANGE)
                        .compareSymbol(CompareSymbol.NOT_EQUAL)
                        .expectedValue(dataStandardDTO.getDataLength())
                        .build();
                warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                String warningLevel = JsonUtil.toJson(warningLevelDTOList);
                BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                        .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                        .warningLevel(warningLevel)
                        .compareWay(CompareWay.VALUE)
                        .build();
                batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
            }
            //长度范围
            if (RANGE.equals(dataStandardDTO.getLengthType())) {
                convertToDataLengthList(dataStandardDTO);
                batchPlanFieldLineDTO.setCountType(CountType.LENGTH_RANGE);
                batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
                //生成每个校验项的配置项
                //两个值都存在生成告警规则
                List<Long> dataLengthList = dataStandardDTO.getDataLengthList();
                String warningLevel = "";
                if (CollectionUtils.isNotEmpty(dataLengthList)
                        && dataLengthList.size() == 2) {
                    WarningLevelDTO firstWarningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .startValue(DEFAULT_START)
                            .endValue(String.valueOf(dataLengthList.get(0) - 1))
                            .compareSymbol(CompareSymbol.EQUAL)
                            .build();
                    WarningLevelDTO secondWarningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .startValue(String.valueOf(dataLengthList.get(1) + 1))
                            .compareSymbol(CompareSymbol.EQUAL)
                            .build();
                    warningLevelDTOList = Arrays.asList(firstWarningLevelDTO
                            , secondWarningLevelDTO);
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                }
                BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                        .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                        .warningLevel(warningLevel)
                        .compareWay(RANGE)
                        .build();
                batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
            }
        }
        //值域
        if (Strings.isNotEmpty(dataStandardDTO.getValueRange())) {
            BatchPlanFieldLineDTO batchPlanFieldLineDTO = BatchPlanFieldLineDTO.builder()
                    .checkWay(CheckWay.COMMON)
                    .checkItem(CheckItem.FIELD_VALUE)
                    .planRuleId(batchPlanFieldDTO.getPlanRuleId())
                    .fieldName(standardAimDTO.getFieldName())
                    .tenantId(standardAimDTO.getTenantId())
                    .build();
            //判断类型，并生成不同的配置项告警规则
            String warningLevel = "";
            WarningLevelDTO warningLevelDTO;
            switch (dataStandardDTO.getValueType()) {
                case StandardValueType.AREA:
                    batchPlanFieldLineDTO.setCountType(CountType.FIXED_VALUE);
                    List<String> valueRangeList = Arrays.asList(dataStandardDTO.getValueRange().split(","));
                    if (CollectionUtils.isNotEmpty(valueRangeList)
                            && valueRangeList.size() == 2) {
                        WarningLevelDTO firstWarningLevelDTO = WarningLevelDTO.builder()
                                .warningLevel(WarningLevel.ORANGE)
                                .startValue(DEFAULT_START)
                                .endValue(valueRangeList.get(0))
                                .compareSymbol(CompareSymbol.EQUAL)
                                .build();
                        WarningLevelDTO secondWarningLevelDTO = WarningLevelDTO.builder()
                                .warningLevel(WarningLevel.ORANGE)
                                .startValue(valueRangeList.get(1))
                                .compareSymbol(CompareSymbol.EQUAL)
                                .build();
                        warningLevelDTOList = Arrays.
                                asList(firstWarningLevelDTO, secondWarningLevelDTO);
                        warningLevel = JsonUtil.toJson(warningLevelDTOList);
                    }
                    break;
                case StandardValueType.ENUM:
                    batchPlanFieldLineDTO.setCountType(CountType.ENUM_VALUE);
                    warningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .compareSymbol(CompareSymbol.INCLUDED)
                            .enumValue(dataStandardDTO.getValueRange())
                            .build();
                    warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                    break;
                case StandardValueType.VALUE_SET:
                    batchPlanFieldLineDTO.setCountType(CountType.LOV_VALUE);
                    warningLevelDTO = WarningLevelDTO.builder()
                            .warningLevel(WarningLevel.ORANGE)
                            .compareSymbol(CompareSymbol.INCLUDED)
                            .enumValue(dataStandardDTO.getValueRange())
                            .build();
                    warningLevelDTOList = Collections.singletonList(warningLevelDTO);
                    warningLevel = JsonUtil.toJson(warningLevelDTOList);
                    break;
                default:
                    break;
            }
            batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
            //生成每个校验项的配置项
            BatchPlanFieldConDTO batchPlanFieldConDTO = BatchPlanFieldConDTO.builder()
                    .planLineId(batchPlanFieldLineDTO.getPlanLineId())
                    .warningLevel(warningLevel)
                    .build();
            //如果校验类型为固定值，默认给范围比较
            if (CountType.FIXED_VALUE.equals(batchPlanFieldLineDTO.getCountType())) {
                batchPlanFieldConDTO.setCompareWay(RANGE);
            }
            batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
        }

        //根据具体落标存入落标关系表
        StandardAimRelationDTO standardAimRelationDTO = StandardAimRelationDTO.builder()
                .aimId(standardAimDTO.getAimId())
                .aimType(AimType.AIM)
                .planId(standardAimDTO.getPlanId())
                .planBaseId(batchPlanBaseDTO.getPlanBaseId())
                .planRuleId(batchPlanFieldDTO.getPlanRuleId())
                .tenantId(standardAimDTO.getTenantId())
                .build();
        standardAimRelationRepository.insertDTOSelective(standardAimRelationDTO);
    }


}
