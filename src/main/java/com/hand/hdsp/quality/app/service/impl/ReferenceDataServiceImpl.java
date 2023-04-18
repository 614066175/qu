package com.hand.hdsp.quality.app.service.impl;

import java.util.List;
import java.util.Objects;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.app.service.ReferenceDataService;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import com.hand.hdsp.quality.domain.entity.ReferenceDataRecord;
import com.hand.hdsp.quality.domain.entity.ReferenceDataValue;
import com.hand.hdsp.quality.domain.repository.ReferenceDataHistoryRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRecordRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataValueRepository;
import com.hand.hdsp.quality.infra.constant.ReferenceDataConstant;
import com.hand.hdsp.quality.workflow.adapter.ReferenceDataOfflineWorkflowAdapter;
import com.hand.hdsp.quality.workflow.adapter.ReferenceDataReleaseWorkflowAdapter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * <p>参考数据头表应用服务默认实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Service
public class ReferenceDataServiceImpl implements ReferenceDataService, ProxySelf<ReferenceDataService> {

    private final ReferenceDataRepository referenceDataRepository;
    private final ReferenceDataRecordRepository referenceDataRecordRepository;
    private final ReferenceDataHistoryRepository referenceDataHistoryRepository;
    private final ReferenceDataValueRepository referenceDataValueRepository;
    private final ReferenceDataReleaseWorkflowAdapter<ReferenceDataDTO, ReferenceDataDTO, Long, String> referenceDataReleaseWorkflowAdapter;
    private final ReferenceDataOfflineWorkflowAdapter<ReferenceDataDTO, ReferenceDataDTO, Long, String> referenceDataOfflineWorkflowAdapter;

    public ReferenceDataServiceImpl(ReferenceDataRepository referenceDataRepository,
                                    ReferenceDataRecordRepository referenceDataRecordRepository,
                                    ReferenceDataHistoryRepository referenceDataHistoryRepository,
                                    ReferenceDataValueRepository referenceDataValueRepository,
                                    ReferenceDataReleaseWorkflowAdapter<ReferenceDataDTO, ReferenceDataDTO, Long, String> referenceDataReleaseWorkflowAdapter,
                                    ReferenceDataOfflineWorkflowAdapter<ReferenceDataDTO, ReferenceDataDTO, Long, String> referenceDataOfflineWorkflowAdapter) {
        this.referenceDataRepository = referenceDataRepository;
        this.referenceDataRecordRepository = referenceDataRecordRepository;
        this.referenceDataHistoryRepository = referenceDataHistoryRepository;
        this.referenceDataValueRepository = referenceDataValueRepository;
        this.referenceDataReleaseWorkflowAdapter = referenceDataReleaseWorkflowAdapter;
        this.referenceDataOfflineWorkflowAdapter = referenceDataOfflineWorkflowAdapter;
    }


    @Override
    public Page<ReferenceDataDTO> list(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO, PageRequest pageRequest) {
        referenceDataDTO.setProjectId(projectId);
        referenceDataDTO.setTenantId(tenantId);
        Page<ReferenceDataDTO> page = referenceDataRepository.list(referenceDataDTO, pageRequest);
        for (ReferenceDataDTO dataDTO : page) {
            queryInstanceId(referenceDataDTO);
            decryptReferenceData(dataDTO);
        }
        return page;
    }

    private void queryInstanceId(ReferenceDataDTO referenceDataDTO) {


    }

    private void decryptReferenceData(ReferenceDataDTO referenceDataDTO) {
        referenceDataDTO.setResponsibleDeptName(decrypt(referenceDataDTO.getResponsibleDeptName()));
        referenceDataDTO.setResponsiblePersonName(decrypt(referenceDataDTO.getResponsiblePersonName()));
        referenceDataDTO.setResponsiblePersonCode(decrypt(referenceDataDTO.getResponsiblePersonCode()));
        referenceDataDTO.setResponsiblePersonTel(decrypt(referenceDataDTO.getResponsiblePersonTel()));
        referenceDataDTO.setResponsiblePersonEmail(decrypt(referenceDataDTO.getResponsiblePersonEmail()));
    }

    private String decrypt(String encrypted) {
        try {
            if (DataSecurityHelper.isTenantOpen() && StringUtils.isNotBlank(encrypted)) {
                return DataSecurityHelper.decrypt(encrypted);
            }
        } catch (Exception e) {
            // ignore
        }
        return encrypted;
    }

    @Override
    public ReferenceDataDTO detail(Long dataId) {
        ReferenceDataDTO detail = referenceDataRepository.detail(dataId);
        decryptReferenceData(detail);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO) {
        referenceDataDTO.setProjectId(projectId);
        referenceDataDTO.setTenantId(tenantId);
        referenceDataDTO.setDataStatus(ReferenceDataConstant.NEW);
        // TODO 考虑参考数据值要不要放这里一起插入
        referenceDataRepository.insertDTOSelective(referenceDataDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO) {
        referenceDataDTO.setProjectId(projectId);
        referenceDataDTO.setTenantId(tenantId);
        referenceDataRepository.updateDTOAllColumnWhereTenant(referenceDataDTO, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO) {
        referenceDataRepository.deleteDTOByPrimaryKey(referenceDataDTO);
        // 删除参考数据值
        referenceDataValueRepository.delete(ReferenceDataValue.builder().dataId(referenceDataDTO.getDataId()).build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemove(Long projectId, Long tenantId, List<ReferenceDataDTO> referenceDataDTOList) {
        if (CollectionUtils.isNotEmpty(referenceDataDTOList)) {
            for (ReferenceDataDTO referenceDataDTO : referenceDataDTOList) {
                self().remove(projectId, tenantId, referenceDataDTO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO) {
        referenceDataDTO.setTenantId(tenantId);
        referenceDataDTO.setProjectId(projectId);
        // 启动发布流程工作流
        referenceDataReleaseWorkflowAdapter.startWorkflow(referenceDataDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRelease(Long projectId, Long tenantId, List<ReferenceDataDTO> referenceDataDTOList) {
        // 启动批量发布流程工作流
        if (CollectionUtils.isNotEmpty(referenceDataDTOList)) {
            for (ReferenceDataDTO referenceDataDTO : referenceDataDTOList) {
                self().release(projectId, tenantId, referenceDataDTO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long projectId, Long tenantId, ReferenceDataDTO referenceDataDTO) {
        referenceDataDTO.setTenantId(tenantId);
        referenceDataDTO.setProjectId(projectId);
        // 启动下线流程工作流
        referenceDataOfflineWorkflowAdapter.startWorkflow(referenceDataDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOffline(Long projectId, Long tenantId, List<ReferenceDataDTO> referenceDataDTOList) {
        // 启动批量下线工作流
        if (CollectionUtils.isNotEmpty(referenceDataDTOList)) {
            for (ReferenceDataDTO referenceDataDTO : referenceDataDTOList) {
                self().offline(projectId, tenantId, referenceDataDTO);
            }
        }
    }

    @Override
    public void releaseCallback(Long tenantId, Long recordId, String nodeApproveResult) {
        // 发布回调逻辑
        referenceDataReleaseWorkflowAdapter.callBack(recordId, nodeApproveResult);
    }

    @Override
    public void offlineCallback(Long tenantId, Long recordId, String nodeApproveResult) {
        // 下线回调逻辑
        referenceDataOfflineWorkflowAdapter.callBack(recordId, nodeApproveResult);
    }

    @Override
    public void withdrawEvent(Long tenantId, Long recordId) {
        ReferenceDataRecord referenceDataRecord = referenceDataRecordRepository.selectByPrimaryKey(recordId);
        if (Objects.isNull(referenceDataRecord) || Objects.isNull(referenceDataRecord.getDataId())) {
            return;
        }
        Long dataId = referenceDataRecord.getDataId();
        ReferenceDataDTO referenceDataDTO = referenceDataRepository.selectDTOByPrimaryKey(dataId);
        if (Objects.isNull(referenceDataDTO)) {
            return;
        }
        referenceDataRecord.setRecordStatus(ReferenceDataConstant.WITHDRAW);
        referenceDataRecordRepository.updateOptional(referenceDataRecord, ReferenceDataRecord.FIELD_RECORD_STATUS);
        Long version = referenceDataHistoryRepository.queryMaxVersion(dataId);
        if (version > 0) {
            // 新建
            referenceDataDTO.setDataStatus(ReferenceDataConstant.NEW);
        } else {
            Integer recordType = referenceDataRecord.getRecordType();
            if (ReferenceDataConstant.RELEASE == recordType) {
                referenceDataDTO.setDataStatus(ReferenceDataConstant.OFFLINE_);
            } else {
                referenceDataDTO.setDataStatus(ReferenceDataConstant.RELEASED);
            }
        }
        referenceDataRepository.updateDTOOptional(referenceDataDTO, ReferenceData.FIELD_DATA_STATUS);
    }
}
