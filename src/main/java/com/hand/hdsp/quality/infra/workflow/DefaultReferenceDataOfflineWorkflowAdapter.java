package com.hand.hdsp.quality.infra.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import com.hand.hdsp.quality.domain.entity.ReferenceDataRecord;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRecordRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRepository;
import com.hand.hdsp.quality.infra.constant.ReferenceDataConstant;
import com.hand.hdsp.quality.workflow.adapter.ReferenceDataOfflineWorkflowAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.platform.plugin.hr.EmployeeHelper;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.RunInstance;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * 默认参考数据工作流下线
 * @author fuqiang.luo@hand-china.com
 */
@Component
public class DefaultReferenceDataOfflineWorkflowAdapter implements ReferenceDataOfflineWorkflowAdapter<ReferenceDataDTO, ReferenceDataDTO, Long, String> {

    private final WorkflowClient workflowClient;
    private final ReferenceDataRecordRepository referenceDataRecordRepository;
    private final ReferenceDataRepository referenceDataRepository;

    public DefaultReferenceDataOfflineWorkflowAdapter(WorkflowClient workflowClient,
                                                      ReferenceDataRecordRepository referenceDataRecordRepository,
                                                      ReferenceDataRepository referenceDataRepository) {
        this.workflowClient = workflowClient;
        this.referenceDataRecordRepository = referenceDataRecordRepository;
        this.referenceDataRepository = referenceDataRepository;
    }

    @Override
    public ReferenceDataDTO startWorkflow(ReferenceDataDTO referenceDataDTO) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        String employeeNum = EmployeeHelper.getEmployeeNum(userId, referenceDataDTO.getTenantId());
        if (StringUtils.isBlank(employeeNum)) {
            // TODO 异常
            throw new CommonException("The current user does not maintain employee information");
        }
        Long dataId = referenceDataDTO.getDataId();
        // 先校验当前参考数据是否在流程中
        int count = referenceDataRecordRepository.selectCountByCondition(Condition.builder(ReferenceDataRecord.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(ReferenceDataRecord.FIELD_DATA_ID, dataId, true)
                        .andEqualTo(ReferenceDataRecord.FIELD_RECORD_STATUS, ReferenceDataConstant.RUNNING))
                .build());
        if (count > 0) {
            // TODO 异常
            throw new CommonException("The current data is already in the process");
        }
        // 先删除原来的记录
        referenceDataRecordRepository.delete(ReferenceDataRecord.builder().dataId(dataId)
                //.recordType(ReferenceDataConstant.OFFLINE)
                .build());
        // 新增记录
        ReferenceDataRecord dataRecord = ReferenceDataRecord
                .builder()
                .dataId(dataId)
                .recordType(ReferenceDataConstant.OFFLINE)
                .recordStatus(ReferenceDataConstant.RUNNING)
                .applyUserId(userId)
                .build();
        referenceDataRecordRepository.insertSelective(dataRecord);
        Map<String, Object> workflowParams = new HashMap<>(4);
        workflowParams.put(ReferenceDataConstant.RESPONSIBLE_PERSON, referenceDataDTO.getResponsiblePersonCode());
        workflowParams.put(ReferenceDataConstant.RECORD_ID, dataRecord.getRecordId());
        workflowParams.put(ReferenceDataConstant.DATA_ID, referenceDataDTO.getDataId());
        String businessKey = String.valueOf(System.currentTimeMillis());
        RunInstance runInstance = workflowClient.startInstanceByFlowKey(referenceDataDTO.getTenantId(), ReferenceDataConstant.OFFLINE_WORKFLOW, businessKey, "EMPLOYEE", employeeNum, workflowParams);
        dataRecord.setInstanceId(runInstance.getInstanceId());
        // 回写
        referenceDataRecordRepository.updateOptional(dataRecord, ReferenceDataRecord.FIELD_INSTANCE_ID);
        return referenceDataDTO;
    }

    @Override
    public String callBack(Long recordId, String approveResult) {
        ReferenceDataRecord referenceDataRecord = referenceDataRecordRepository.selectByPrimaryKey(recordId);
        if (Objects.isNull(referenceDataRecord) || Objects.isNull(referenceDataRecord.getDataId())) {
            return approveResult;
        }
        ReferenceDataDTO referenceDataDTO = referenceDataRepository.selectDTOByPrimaryKey(referenceDataRecord.getDataId());
        if (Objects.isNull(referenceDataDTO)) {
            return approveResult;
        }
        if(WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)){
            // 同意
            referenceDataDTO.setDataStatus(ReferenceDataConstant.OFFLINE_);
            referenceDataDTO.setReleaseBy(null);
            referenceDataDTO.setReleaseDate(null);
            referenceDataRepository.updateDTOOptional(referenceDataDTO,
                    ReferenceData.FIELD_DATA_STATUS,
                    ReferenceData.FIELD_RELEASE_BY,
                    ReferenceData.FIELD_RELEASE_DATE);
            referenceDataRecord.setRecordStatus(ReferenceDataConstant.PASS);
        }
        referenceDataRecord.setRecordStatus(ReferenceDataConstant.REJECT);
        referenceDataRecordRepository.updateOptional(referenceDataRecord, ReferenceDataRecord.FIELD_RECORD_STATUS);
        return approveResult;
    }
}
