package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.infra.vo.PlanGroupTreeVO;

import java.util.List;

/**
 * <p>评估方案分组表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanGroupRepository extends BaseRepository<PlanGroup, PlanGroupDTO>, ProxySelf<PlanGroupRepository> {

    /**
     * 含批数据估方案列表
     *
     * @param planGroupTreeVO
     * @return
     */
    List<PlanGroupTreeVO> treeBatch(PlanGroupTreeVO planGroupTreeVO);

    /**
     * 含实时数据估方案列表
     *
     * @param planGroupTreeVO
     * @return
     */
    List<PlanGroupTreeVO> treeStream(PlanGroupTreeVO planGroupTreeVO);
}
