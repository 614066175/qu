package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.ReferenceDataDTO;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.infra.mapper.ReferenceDataMapper;

import java.util.List;

/**
 * <p>参考数据头表资源库实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Component
public class ReferenceDataRepositoryImpl extends BaseRepositoryImpl<ReferenceData, ReferenceDataDTO> implements ReferenceDataRepository {

    private final ReferenceDataMapper referenceDataMapper;

    public ReferenceDataRepositoryImpl(ReferenceDataMapper referenceDataMapper) {
        this.referenceDataMapper = referenceDataMapper;
    }

    @Override
    public Page<ReferenceDataDTO> list(ReferenceDataDTO referenceDataDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> referenceDataMapper.list(referenceDataDTO));
    }

    @Override
    public List<ReferenceDataDTO> list(ReferenceDataDTO referenceDataDTO) {
        return referenceDataMapper.list(referenceDataDTO);
    }


    @Override
    public ReferenceDataDTO detail(Long dataId) {
        return referenceDataMapper.detail(dataId);
    }
}
