package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceDataValue;
import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;
import com.hand.hdsp.quality.domain.repository.ReferenceDataValueRepository;
import com.hand.hdsp.quality.infra.mapper.ReferenceDataValueMapper;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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
