package com.hand.hdsp.quality.app.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.hand.hdsp.core.util.JSON;
import com.hand.hdsp.core.util.PageParseUtil;
import com.hand.hdsp.quality.api.dto.ReferenceDataHistoryDTO;
import com.hand.hdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import com.hand.hdsp.quality.app.service.ReferenceDataHistoryService;
import com.hand.hdsp.quality.domain.entity.ReferenceDataHistory;
import com.hand.hdsp.quality.domain.repository.ReferenceDataHistoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * <p>参考数据头表应用服务默认实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Service
public class ReferenceDataHistoryServiceImpl implements ReferenceDataHistoryService {

    private final ReferenceDataHistoryRepository referenceDataHistoryRepository;

    public ReferenceDataHistoryServiceImpl(ReferenceDataHistoryRepository referenceDataHistoryRepository) {
        this.referenceDataHistoryRepository = referenceDataHistoryRepository;
    }

    @Override
    public Page<ReferenceDataHistoryDTO> list(Long projectId, Long tenantId, ReferenceDataHistoryDTO referenceDataHistoryDTO, PageRequest pageRequest) {
        referenceDataHistoryDTO.setProjectId(projectId);
        referenceDataHistoryDTO.setTenantId(tenantId);
        Long dataId = referenceDataHistoryDTO.getDataId();
        if (Objects.isNull(dataId)) {
            // TODO 异常信息
            throw new CommonException("Please select a reference data");
        }
        Page<ReferenceDataHistoryDTO> list = referenceDataHistoryRepository.list(dataId, pageRequest);
        Long maxVersion = referenceDataHistoryRepository.queryMaxVersion(dataId);
        for (ReferenceDataHistoryDTO dataHistoryDTO : list) {
            if (maxVersion.equals(dataHistoryDTO.getVersionNumber())) {
                dataHistoryDTO.setCurrentVersion(1);
            } else {
                dataHistoryDTO.setCurrentVersion(0);
            }
        }
        return list;
    }

    @Override
    public ReferenceDataHistoryDTO detail(Long historyId) {
        ReferenceDataHistoryDTO detail = referenceDataHistoryRepository.detail(historyId);
        decryptReferenceDataHistory(detail);
        String dataValueJson = detail.getDataValueJson();
        if (StringUtils.isNotBlank(dataValueJson)) {
            List<SimpleReferenceDataValueDTO> referenceDataValueDTOList = JSON.toArray(dataValueJson, SimpleReferenceDataValueDTO.class);
            detail.setReferenceDataValueDTOList(referenceDataValueDTOList);
        }
        return detail;
    }

    @Override
    public Page<SimpleReferenceDataValueDTO> values(Long historyId, PageRequest pageRequest) {
        ReferenceDataHistory referenceDataHistory = referenceDataHistoryRepository.selectByPrimaryKey(historyId);
        String dataValueJson = Optional.ofNullable(referenceDataHistory.getDataValueJson()).orElse("[]");
        List<SimpleReferenceDataValueDTO> referenceDataValueDTOList = JSON.toArray(dataValueJson, SimpleReferenceDataValueDTO.class);
        return PageParseUtil.springPage2C7nPage(PageUtil.doPage(referenceDataValueDTOList, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize())));
    }

    private void decryptReferenceDataHistory(ReferenceDataHistoryDTO referenceDataHistoryDTO) {
        referenceDataHistoryDTO.setResponsibleDeptName(decrypt(referenceDataHistoryDTO.getResponsibleDeptName()));
        referenceDataHistoryDTO.setResponsiblePersonName(decrypt(referenceDataHistoryDTO.getResponsiblePersonName()));
        referenceDataHistoryDTO.setResponsiblePersonCode(decrypt(referenceDataHistoryDTO.getResponsiblePersonCode()));
        referenceDataHistoryDTO.setResponsiblePersonTel(decrypt(referenceDataHistoryDTO.getResponsiblePersonTel()));
        referenceDataHistoryDTO.setResponsiblePersonEmail(decrypt(referenceDataHistoryDTO.getResponsiblePersonEmail()));
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
    public void remove(Long projectId, Long tenantId, ReferenceDataHistoryDTO referenceDataHistoryDTO) {
        referenceDataHistoryRepository.deleteDTOByPrimaryKey(referenceDataHistoryDTO);
    }
}
