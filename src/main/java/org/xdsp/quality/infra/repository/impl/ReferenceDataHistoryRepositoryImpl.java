package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.ReferenceDataHistoryDTO;
import org.xdsp.quality.domain.entity.ReferenceDataHistory;
import org.xdsp.quality.domain.repository.ReferenceDataHistoryRepository;
import org.xdsp.quality.infra.mapper.ReferenceDataHistoryMapper;

import java.util.Objects;

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
