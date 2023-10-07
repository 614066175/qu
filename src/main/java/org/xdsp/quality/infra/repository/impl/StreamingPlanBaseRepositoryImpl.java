package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StreamingPlanBaseDTO;
import org.xdsp.quality.domain.entity.StreamingPlanBase;
import org.xdsp.quality.domain.repository.StreamingPlanBaseRepository;
import org.xdsp.quality.infra.mapper.StreamingPlanBaseMapper;

/**
 * <p>实时数据方案-基础配置表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanBaseRepositoryImpl extends BaseRepositoryImpl<StreamingPlanBase, StreamingPlanBaseDTO> implements StreamingPlanBaseRepository {

    private final StreamingPlanBaseMapper streamingPlanBaseMapper;

    public StreamingPlanBaseRepositoryImpl(StreamingPlanBaseMapper streamingPlanBaseMapper) {
        this.streamingPlanBaseMapper = streamingPlanBaseMapper;
    }

    @Override
    public Page<StreamingPlanBaseDTO> list(PageRequest pageRequest, StreamingPlanBaseDTO streamingPlanBaseDTO) {
        return PageHelper.doPage(pageRequest, () -> streamingPlanBaseMapper.list(streamingPlanBaseDTO));
    }
}
