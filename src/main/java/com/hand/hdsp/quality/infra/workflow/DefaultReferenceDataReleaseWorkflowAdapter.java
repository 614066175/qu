package com.hand.hdsp.quality.infra.workflow;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.JSON;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import com.hand.hdsp.quality.domain.entity.ReferenceDataHistory;
import com.hand.hdsp.quality.domain.entity.ReferenceDataRecord;
import com.hand.hdsp.quality.domain.repository.ReferenceDataHistoryRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRecordRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataValueRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.ReferenceDataConstant;
import com.hand.hdsp.quality.workflow.adapter.ReferenceDataReleaseWorkflowAdapter;
import org.apache.commons.collections4.CollectionUtils;
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
 * 默认参考数据发布工作流
 * @author fuqiang.luo@hand-china.com
 */
@Component
public class DefaultReferenceDataReleaseWorkflowAdapter implements ReferenceDataReleaseWorkflowAdapter<ReferenceDataDTO, ReferenceDataDTO, Long, String> {

    private final WorkflowClient workflowClient;
    private final ReferenceDataRecordRepository referenceDataRecordRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private final ReferenceDataHistoryRepository referenceDataHistoryRepository;
    private final ReferenceDataValueRepository referenceDataValueRepository;
    private final CommonGroupRepository commonGroupRepository;
    private final Lock lock = new ReentrantLock();

    public DefaultReferenceDataReleaseWorkflowAdapter(WorkflowClient workflowClient,
                                                      ReferenceDataRecordRepository referenceDataRecordRepository,
                                                      ReferenceDataRepository referenceDataRepository,
                                                      ReferenceDataHistoryRepository referenceDataHistoryRepository,
                                                      ReferenceDataValueRepository referenceDataValueRepository,
                                                      CommonGroupRepository commonGroupRepository) {
        this.workflowClient = workflowClient;
        this.referenceDataRecordRepository = referenceDataRecordRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.referenceDataHistoryRepository = referenceDataHistoryRepository;
        this.referenceDataValueRepository = referenceDataValueRepository;
        this.commonGroupRepository = commonGroupRepository;
    }


    @Override
    public ReferenceDataDTO startWorkflow(ReferenceDataDTO referenceDataDTO) {
        lock.lock();
        try {
            Long userId = DetailsHelper.getUserDetails().getUserId();
            String employeeNum = EmployeeHelper.getEmployeeNum(userId, referenceDataDTO.getTenantId());
            if (StringUtils.isBlank(employeeNum)) {
                throw new CommonException(ErrorCode.USER_NO_EMPLOYEE);
            }
            Long dataId = referenceDataDTO.getDataId();
            // 先校验当前参考数据是否在流程中
            int count = referenceDataRecordRepository.selectCountByCondition(Condition.builder(ReferenceDataRecord.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(ReferenceDataRecord.FIELD_DATA_ID, dataId, true)
                            .andEqualTo(ReferenceDataRecord.FIELD_RECORD_STATUS, ReferenceDataConstant.RUNNING))
                    .build());
            if (count > 0) {
                throw new CommonException(ErrorCode.DATA_IN_PROCESS);
            }
            List<SimpleReferenceDataValueDTO> simpleReferenceDataValueDTOList = referenceDataValueRepository.simpleQueryByDataId(dataId);
            if (CollectionUtils.isEmpty(simpleReferenceDataValueDTOList)) {
                throw new CommonException(ErrorCode.DATA_NO_VALUE);
            }
            // 先删除原来的记录
            referenceDataRecordRepository.delete(ReferenceDataRecord.builder()
                    .dataId(dataId)
                    // .recordType(ReferenceDataConstant.RELEASE)
                    .build());
            // 新增记录
            ReferenceDataRecord dataRecord = ReferenceDataRecord
                    .builder()
                    .dataId(dataId)
                    .recordType(ReferenceDataConstant.RELEASE)
                    .recordStatus(ReferenceDataConstant.RUNNING)
                    .applyUserId(userId)
                    .build();
            referenceDataRecordRepository.insertSelective(dataRecord);
            Map<String, Object> workflowParams = new HashMap<>(4);
            workflowParams.put(ReferenceDataConstant.RESPONSIBLE_PERSON, referenceDataDTO.getResponsiblePersonCode());
            workflowParams.put(ReferenceDataConstant.RECORD_ID, dataRecord.getRecordId());
            workflowParams.put(ReferenceDataConstant.DATA_ID, referenceDataDTO.getDataId());
            String businessKey = String.valueOf(System.currentTimeMillis());
            RunInstance runInstance = workflowClient.startInstanceByFlowKey(referenceDataDTO.getTenantId(), ReferenceDataConstant.RELEASE_WORKFLOW, businessKey, "EMPLOYEE", employeeNum, workflowParams);
            dataRecord.setInstanceId(runInstance.getInstanceId());
            referenceDataDTO.setDataStatus(ReferenceDataConstant.RELEASE_ING);
            // 回写
            referenceDataRecordRepository.updateOptional(dataRecord, ReferenceDataRecord.FIELD_INSTANCE_ID);
            referenceDataRepository.updateDTOOptional(referenceDataDTO, ReferenceData.FIELD_DATA_STATUS);
            return referenceDataDTO;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String callBack(Long recordId, String approveResult) {
        ReferenceDataRecord referenceDataRecord = referenceDataRecordRepository.selectByPrimaryKey(recordId);
        if (Objects.isNull(referenceDataRecord) || Objects.isNull(referenceDataRecord.getDataId())) {
            return approveResult;
        }
        Long dataId = referenceDataRecord.getDataId();
        ReferenceDataDTO referenceDataDTO = referenceDataRepository.selectDTOByPrimaryKey(dataId);
        if (Objects.isNull(referenceDataDTO)) {
            return approveResult;
        }
        Long maxVersion = referenceDataHistoryRepository.queryMaxVersion(dataId);
        if(WorkflowConstant.ApproveAction.APPROVED.equals(approveResult)){
            // 同意
            Date date = new Date();
            referenceDataDTO.setDataStatus(ReferenceDataConstant.RELEASED);
            referenceDataDTO.setReleaseBy(referenceDataRecord.getApplyUserId());
            referenceDataDTO.setReleaseDate(date);
            referenceDataRecord.setRecordStatus(ReferenceDataConstant.PASS);
            // 存储版本
            List<SimpleReferenceDataValueDTO> simpleReferenceDataValueDTOList = referenceDataValueRepository.simpleQueryByDataId(dataId);
            ReferenceDataHistory history = ReferenceDataHistory
                    .builder()
                    .dataId(referenceDataDTO.getDataId())
                    .dataCode(referenceDataDTO.getDataCode())
                    .dataName(referenceDataDTO.getDataName())
                    .dataDesc(referenceDataDTO.getDataDesc())
                    .parentDataId(referenceDataDTO.getParentDataId())
                    .dataGroupId(referenceDataDTO.getDataGroupId())
                    .dataValueJson(JSON.toJson(simpleReferenceDataValueDTOList))
                    .versionNumber(maxVersion + 1)
                    .releaseBy(referenceDataRecord.getApplyUserId())
                    .releaseDate(date)
                    .responsibleDeptId(referenceDataDTO.getResponsibleDeptId())
                    .responsiblePersonId(referenceDataDTO.getResponsiblePersonId())
                    .responsiblePersonTel(referenceDataDTO.getResponsiblePersonTel())
                    .responsiblePersonEmail(referenceDataDTO.getResponsiblePersonEmail())
                    .projectId(referenceDataDTO.getProjectId())
                    .tenantId(referenceDataDTO.getTenantId())
                    .build();
            if (Objects.nonNull(referenceDataDTO.getDataGroupId())) {
                CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(referenceDataDTO.getDataGroupId());
                if (Objects.nonNull(commonGroup)) {
                    history.setGroupPath(commonGroup.getGroupPath());
                }
            }
            if (Objects.nonNull(referenceDataDTO.getParentDataId())) {
                ReferenceDataDTO parent = referenceDataRepository.selectDTOByPrimaryKey(referenceDataDTO.getParentDataId());
                if (Objects.nonNull(parent)) {
                    history.setParentDataCode(parent.getDataCode());
                    history.setParentDataName(parent.getDataName());
                }
            }
            referenceDataHistoryRepository.insertSelective(history);
        } else {
            referenceDataRecord.setRecordStatus(ReferenceDataConstant.REJECT);
            // 判断之前是什么状态
            if (maxVersion == 0) {
                // 没有发布过 为新建
                referenceDataDTO.setDataStatus(ReferenceDataConstant.NEW);
            } else {
                // 有发布过 就是下线
                referenceDataDTO.setDataStatus(ReferenceDataConstant.OFFLINE_);
            }
        }
        referenceDataRecordRepository.updateOptional(referenceDataRecord, ReferenceDataRecord.FIELD_RECORD_STATUS);
        referenceDataRepository.updateDTOOptional(referenceDataDTO,
                ReferenceData.FIELD_DATA_STATUS,
                ReferenceData.FIELD_RELEASE_BY,
                ReferenceData.FIELD_RELEASE_DATE);
        return approveResult;
    }
}
