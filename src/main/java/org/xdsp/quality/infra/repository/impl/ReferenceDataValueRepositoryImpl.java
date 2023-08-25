package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.ReferenceDataValueDTO;
import org.xdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import org.xdsp.quality.domain.entity.ReferenceDataValue;
import org.xdsp.quality.domain.repository.ReferenceDataValueRepository;
import org.xdsp.quality.infra.mapper.ReferenceDataValueMapper;

import java.util.List;

/**
 * <p>参考数据值资源库实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Component
public class ReferenceDataValueRepositoryImpl extends BaseRepositoryImpl<ReferenceDataValue, ReferenceDataValueDTO> implements ReferenceDataValueRepository {

    private final ReferenceDataValueMapper referenceDataValueMapper;

    public ReferenceDataValueRepositoryImpl(ReferenceDataValueMapper referenceDataValueMapper) {
        this.referenceDataValueMapper = referenceDataValueMapper;
    }

    @Override
    public Page<ReferenceDataValueDTO> list(ReferenceDataValueDTO referenceDataValueDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> referenceDataValueMapper.list(referenceDataValueDTO));
    }

    @Override
    public List<ReferenceDataValueDTO> list(ReferenceDataValueDTO referenceDataValueDTO) {
        return referenceDataValueMapper.list(referenceDataValueDTO);
    }

    @Override
    public List<SimpleReferenceDataValueDTO> simpleQueryByDataId(Long dataId) {
        return referenceDataValueMapper.simpleQueryByDataId(dataId);
    }
}
