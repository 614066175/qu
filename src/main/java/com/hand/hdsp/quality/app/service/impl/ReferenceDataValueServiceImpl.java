package com.hand.hdsp.quality.app.service.impl;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;
import com.hand.hdsp.quality.app.service.ReferenceDataValueService;
import com.hand.hdsp.quality.domain.repository.ReferenceDataValueRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>参考数据值应用服务默认实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Service
public class ReferenceDataValueServiceImpl implements ReferenceDataValueService, ProxySelf<ReferenceDataValueService> {

    private final ReferenceDataValueRepository referenceDataValueRepository;

    public ReferenceDataValueServiceImpl(ReferenceDataValueRepository referenceDataValueRepository) {
        this.referenceDataValueRepository = referenceDataValueRepository;
    }

    @Override
    public Page<ReferenceDataValueDTO> list(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO, PageRequest pageRequest) {
        referenceDataValueDTO.setProjectId(projectId);
        referenceDataValueDTO.setTenantId(tenantId);
        return referenceDataValueRepository.list(referenceDataValueDTO, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO) {
        referenceDataValueDTO.setProjectId(projectId);
        referenceDataValueDTO.setTenantId(tenantId);
        referenceDataValueRepository.insertDTOSelective(referenceDataValueDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreate(Long projectId, Long tenantId, List<ReferenceDataValueDTO> referenceDataValueDTOList) {
        if (CollectionUtils.isNotEmpty(referenceDataValueDTOList)) {
            for (ReferenceDataValueDTO referenceDataValueDTO : referenceDataValueDTOList) {
                self().create(projectId, tenantId, referenceDataValueDTO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO) {
        referenceDataValueDTO.setProjectId(projectId);
        referenceDataValueDTO.setTenantId(tenantId);
        referenceDataValueRepository.updateDTOAllColumnWhereTenant(referenceDataValueDTO, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(Long projectId, Long tenantId, List<ReferenceDataValueDTO> referenceDataValueDTOList) {
        if (CollectionUtils.isNotEmpty(referenceDataValueDTOList)) {
            for (ReferenceDataValueDTO referenceDataValueDTO : referenceDataValueDTOList) {
                self().update(projectId, tenantId, referenceDataValueDTO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long projectId, Long tenantId, ReferenceDataValueDTO referenceDataValueDTO) {
        referenceDataValueDTO.setProjectId(projectId);
        referenceDataValueDTO.setTenantId(tenantId);
        referenceDataValueRepository.deleteDTOByPrimaryKey(referenceDataValueDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemove(Long projectId, Long tenantId, List<ReferenceDataValueDTO> referenceDataValueDTOList) {
        if (CollectionUtils.isNotEmpty(referenceDataValueDTOList)) {
            for (ReferenceDataValueDTO referenceDataValueDTO : referenceDataValueDTOList) {
                self().remove(projectId, tenantId, referenceDataValueDTO);
            }
        }
    }
}
