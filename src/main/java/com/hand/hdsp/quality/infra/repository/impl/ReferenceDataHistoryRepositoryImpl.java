package com.hand.hdsp.quality.infra.repository.impl;

import java.util.Objects;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.ReferenceDataHistory;
import com.hand.hdsp.quality.api.dto.ReferenceDataHistoryDTO;
import com.hand.hdsp.quality.domain.repository.ReferenceDataHistoryRepository;
import com.hand.hdsp.quality.infra.mapper.ReferenceDataHistoryMapper;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>参考数据头表资源库实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Component
public class ReferenceDataHistoryRepositoryImpl extends BaseRepositoryImpl<ReferenceDataHistory, ReferenceDataHistoryDTO> implements ReferenceDataHistoryRepository {


    private final ReferenceDataHistoryMapper referenceDataHistoryMapper;

    public ReferenceDataHistoryRepositoryImpl(ReferenceDataHistoryMapper referenceDataHistoryMapper) {
        this.referenceDataHistoryMapper = referenceDataHistoryMapper;
    }


    @Override
    public Long queryMaxVersion(Long dataId) {
        Long maxVersion = referenceDataHistoryMapper.queryMaxVersion(dataId);
        if (Objects.isNull(maxVersion)) {
            maxVersion = 0L;
        }
        return maxVersion;
    }

    @Override
    public Page<ReferenceDataHistoryDTO> list(Long dataId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> referenceDataHistoryMapper.queryByDataId(dataId));
    }

    @Override
    public ReferenceDataHistoryDTO detail(Long historyId) {
        return referenceDataHistoryMapper.detail(historyId);
    }
}
