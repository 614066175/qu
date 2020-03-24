package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.domain.entity.StreamingPlanBase;

/**
 * <p>实时数据方案-基础配置表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanBaseRepository extends BaseRepository<StreamingPlanBase, StreamingPlanBaseDTO>, ProxySelf<StreamingPlanBaseRepository> {

}
