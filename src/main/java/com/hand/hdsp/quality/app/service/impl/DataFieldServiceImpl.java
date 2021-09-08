package com.hand.hdsp.quality.app.service.impl;


import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import java.util.*;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.dto.RunInstance;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>字段标准表应用服务默认实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Slf4j
@Service
public class DataFieldServiceImpl implements DataFieldService {

    public static final Long DEFAULT_VERSION = 1L;

    private final DataFieldRepository dataFieldRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final StandardApproveRepository standardApproveRepository;

    private final DataFieldVersionRepository dataFieldVersionRepository;

    private final DataFieldMapper dataFieldMapper;

    private final DataStandardService dataStandardService;

    private final StandardAimRepository standardAimRepository;

    private final ExtraVersionRepository extraVersionRepository;

    private final DataStandardMapper dataStandardMapper;

    @Value("${hdsp.workflow.enabled:false}")
    private boolean enableWorkflow;

    @Autowired
    private WorkflowClient workflowClient;


    public DataFieldServiceImpl(DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository, StandardApproveRepository standardApproveRepository, DataFieldVersionRepository dataFieldVersionRepository, DataFieldMapper dataFieldMapper, DataStandardService dataStandardService, StandardAimRepository standardAimRepository, ExtraVersionRepository extraVersionRepository, DataStandardMapper dataStandardMapper) {
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.standardApproveRepository = standardApproveRepository;
        this.dataFieldVersionRepository = dataFieldVersionRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardService = dataStandardService;
        this.standardAimRepository = standardAimRepository;
        this.extraVersionRepository = extraVersionRepository;
        this.dataStandardMapper = dataStandardMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DataFieldDTO dataFieldDTO) {
        List<DataFieldDTO> dataFieldDTOS = dataFieldRepository.selectDTOByCondition(Condition.builder(DataField.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                        .andEqualTo(DataField.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(dataFieldDTOS)) {
            throw new CommonException(ErrorCode.DATA_FIELD_NAME_EXIST);
        }

        dataFieldDTO.setStandardStatus(CREATE);
        dataFieldRepository.insertDTOSelective(dataFieldDTO);

        List<StandardExtraDTO> standardExtraDTOList = dataFieldDTO.getStandardExtraDTOList();
        if (CollectionUtils.isNotEmpty(standardExtraDTOList)) {
            standardExtraDTOList.forEach(s -> {
                StandardExtraDTO extraDTO = StandardExtraDTO.builder()
                        .standardId(dataFieldDTO.getFieldId())
                        .extraKey(s.getExtraKey())
                        .extraValue(s.getExtraValue())
                        .standardType(FIELD)
                        .tenantId(dataFieldDTO.getTenantId())
                        .build();
                standardExtraRepository.insertDTOSelective(extraDTO);
            });
        }
    }

    @Override
    public DataFieldDTO detail(Long tenantId, Long fieldId) {
        List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(DataFieldDTO
                .builder()
                .fieldId(fieldId)
                .build());
        if (CollectionUtils.isEmpty(dataFieldDTOList)) {
            throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
        }
        DataFieldDTO dataFieldDTO = dataFieldDTOList.get(0);
        //判断当前租户是否启用安全加密
        if (dataStandardMapper.isEncrypt(tenantId) == 1) {
            //解密邮箱，电话
            if (Strings.isNotEmpty(dataFieldDTO.getChargeTel())) {
                dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel()));
            }
            if (Strings.isNotEmpty(dataFieldDTO.getChargeEmail())) {
                dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail()));
            }
            if (Strings.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
                dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName()));
            }
        }
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, fieldId)
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        dataFieldDTO.setStandardExtraDTOList(standardExtraDTOS);
        return dataFieldDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataFieldDTO dataFieldDTO) {
        if (ONLINE.equals(dataFieldDTO.getStandardStatus())
                || OFFLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
            throw new CommonException(ErrorCode.DATA_FIELD_CAN_NOT_DELETE);
        }
//        if (ONLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
//            //删除申请记录表记录
//            List<StandardApproveDTO> standardApproveDTOS = standardApproveRepository.selectDTOByCondition(Condition.builder(StandardApprove.class)
//                    .andWhere(Sqls.custom()
//                            .andEqualTo(StandardApprove.FIELD_STANDARD_NAME, dataFieldDTO.getFieldName())
//                            .andEqualTo(StandardApprove.FIELD_STANDARD_TYPE, FIELD)
//                            .andEqualTo(StandardApprove.FIELD_OPERATION, OFFLINE_APPROVING)
//                            .andEqualTo(StandardApprove.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
//                    .build());
//            if (CollectionUtils.isNotEmpty(standardApproveDTOS)) {
//                standardApproveRepository.deleteDTO(standardApproveDTOS.get(0));
//            }
//        }
        dataFieldRepository.deleteByPrimaryKey(dataFieldDTO);
        //删除版本表数据
        List<DataFieldVersionDTO> dataStandardVersionDTOS = dataFieldVersionRepository.selectDTOByCondition(Condition.builder(DataFieldVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataFieldVersion.FIELD_FIELD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(DataFieldVersion.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                .build());
        dataFieldVersionRepository.batchDTODeleteByPrimaryKey(dataStandardVersionDTOS);
        // 删除额外信息
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                ).build());
        //todo 删除落标表
        standardExtraRepository.batchDTODeleteByPrimaryKey(standardExtraDTOS);
    }

    @Override
    public Page<DataFieldDTO> list(PageRequest pageRequest, DataFieldDTO dataFieldDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataFieldMapper.list(dataFieldDTO));
    }

    @Override
    public void updateStatus(DataFieldDTO dataFieldDTO) {
        DataFieldDTO oldDataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
        if (Objects.isNull(oldDataFieldDTO)) {
            throw new CommonException(ErrorCode.DATA_FIELD_NAME_EXIST);
        }
        oldDataFieldDTO.setStandardStatus(dataFieldDTO.getStandardStatus());
        dataFieldRepository.updateByDTOPrimaryKey(oldDataFieldDTO);
    }

    @Override
    public void aim(Long tenantId, List<StandardAimDTO> standardAimDTOList) {
        dataStandardService.aim(tenantId, standardAimDTOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishOrOff(DataFieldDTO dataFieldDTO) {
        if (enableWorkflow) {
            //开启工作流
            //根据上下线状态开启不同的工作流实例
            if(ONLINE.equals(dataFieldDTO.getStandardStatus())){
                //先修改状态再启动工作流，启动工作流需要花费一定时间,有异常回滚
                this.workflowing(dataFieldDTO.getTenantId(),dataFieldDTO.getFieldId(),ONLINE_APPROVING);
                this.startWorkFlow(WorkFlowConstant.FieldStandard.ONLINE_WORKFLOW_KEY,dataFieldDTO);
            }
            if(OFFLINE.equals(dataFieldDTO.getStandardStatus())){
                this.workflowing(dataFieldDTO.getTenantId(),dataFieldDTO.getFieldId(),OFFLINE_APPROVING);
                this.startWorkFlow(WorkFlowConstant.FieldStandard.OFFLINE_WORKFLOW_KEY,dataFieldDTO);
            }
        } else {
            DataFieldDTO dto = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
            if (Objects.isNull(dto)) {
                throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
            }
            dataFieldDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
            dataFieldRepository.updateDTOOptional(dataFieldDTO, DataField.FIELD_STANDARD_STATUS);
            if (ONLINE.equals(dataFieldDTO.getStandardStatus())) {
                //存版本表
                doVersion(dataFieldDTO);
            }
        }
    }

    private void startWorkFlow(String workflowKey, DataFieldDTO dataFieldDTO) {
        //使用当前时间戳作为业务主键
        String bussinessKey=String.valueOf(System.currentTimeMillis());
        Map<String, Object> var = new HashMap<>();
        //给流程变量
        var.put("fieldId", dataFieldDTO.getFieldId());
        //使用自研工作流客户端
        workflowClient.startInstanceByFlowKey(dataFieldDTO.getTenantId(), workflowKey, bussinessKey, "USER", String.valueOf(DetailsHelper.getUserDetails().getUserId()), var);
    }


    @Override
    public List<DataFieldDTO> export(DataFieldDTO dto, ExportParam exportParam, PageRequest pageRequest) {
        Page<DataFieldDTO> list = list(pageRequest, dto);
        if(list == null){ return new ArrayList<>();}
        for (DataFieldDTO dataFieldDTO : list) {
            // 如果开启了数据加密
            if(dataStandardMapper.isEncrypt(dataFieldDTO.getTenantId()) == 1){
                decodeForDataFieldDTO(dataFieldDTO);
            }
        }
        return list;
    }

    /**
     * 保证导入导出数据的一致性
     * @param dataFieldDTO
     */
    public void decodeForDataFieldDTO(DataFieldDTO dataFieldDTO){
        // 解密电话号码
        if(StringUtils.isNotEmpty(dataFieldDTO.getChargeTel())){ dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel())); }
        // 解密邮箱地址
        if(StringUtils.isNotEmpty(dataFieldDTO.getChargeEmail())){ dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail())); }
        // 解密部门名称
        if(StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())){ dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName())); }
    }

    private void doVersion(DataFieldDTO dataFieldDTO) {
        Long lastVersion = DEFAULT_VERSION;
        List<DataFieldVersionDTO> dataFieldVersionDTOS = dataFieldVersionRepository.selectDTOByCondition(Condition.builder(DataFieldVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_ID, dataFieldDTO.getFieldId()))
                .orderByDesc(DataFieldVersion.FIELD_VERSION_NUMBER).build());
        DataFieldVersionDTO dataFieldVersionDTO = new DataFieldVersionDTO();
        //不为空则取最新版本
        if (CollectionUtils.isNotEmpty(dataFieldVersionDTOS)) {
            lastVersion = dataFieldVersionDTOS.get(0).getVersionNumber() + 1;
        }
        //存入版本表
        BeanUtils.copyProperties(dataFieldDTO, dataFieldVersionDTO);
        dataFieldVersionDTO.setVersionNumber(lastVersion);
        dataFieldVersionRepository.insertDTOSelective(dataFieldVersionDTO);
        //存附加信息版本表
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
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
    public void onlineWorkflowSuccess(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, ONLINE);
    }

    @Override
    public void onlineWorkflowFail(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, OFFLINE);
    }

    @Override
    public void offlineWorkflowSuccess(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, OFFLINE);
    }

    @Override
    public void offlineWorkflowFail(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, ONLINE);
    }

    @Override
    public void onlineWorkflowing(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, ONLINE_APPROVING);
    }

    @Override
    public void offlineWorkflowing(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, OFFLINE_APPROVING);
    }

    @Override
    public List<AssigneeUserDTO> findCharger(Long tenantId, Long fieldId) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(
                DataFieldDTO.builder().fieldId(fieldId).tenantId(tenantId).build()
        );
        if (dataFieldDTO != null) {
            //查询责任人
            return Arrays.asList(dataStandardMapper.selectAssigneeUser(dataFieldDTO.getChargeId()));
        } else {
            throw new CommonException(ErrorCode.NOT_FIND_VALUE);
        }
    }

    /**
     * 指定字段标准修改状态，供审批中，审批结束任务状态变更
     *
     * @param tenantId
     * @param fieldId
     * @param status
     */
    private void workflowing(Long tenantId, Long fieldId, String status) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(
                DataFieldDTO.builder().fieldId(fieldId).tenantId(tenantId).build()
        );
        if (dataFieldDTO != null) {
            dataFieldDTO.setStandardStatus(status);
            dataFieldRepository.updateDTOOptional(dataFieldDTO, DataStandard.FIELD_STANDARD_STATUS);
            if (ONLINE.equals(status)) {
                doVersion(dataFieldDTO);
            }
        }
    }
}
