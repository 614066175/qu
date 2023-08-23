package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.infra.vo.PlanGroupTreeVO;

import java.util.List;

/**
 * <p>评估方案分组表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanGroupRepository extends BaseRepository<PlanGroup, PlanGroupDTO>, ProxySelf<PlanGroupRepository> {

    /**
     *
     * 含评估方案列表
     *
     * @param planGroupTreeVO
     * @return
     */
    List<PlanGroupTreeVO> tree(PlanGroupTreeVO planGroupTreeVO);
}
