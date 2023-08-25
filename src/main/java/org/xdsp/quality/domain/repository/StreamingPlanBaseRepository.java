package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StreamingPlanBaseDTO;
import org.xdsp.quality.domain.entity.StreamingPlanBase;

/**
 * <p>实时数据方案-基础配置表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanBaseRepository extends BaseRepository<StreamingPlanBase, StreamingPlanBaseDTO>, ProxySelf<StreamingPlanBaseRepository> {

    /**
     * 行列表
     *
     * @param pageRequest
     * @param streamingPlanBaseDTO
     * @return
     */
    Page<StreamingPlanBaseDTO> list(PageRequest pageRequest, StreamingPlanBaseDTO streamingPlanBaseDTO);
}
