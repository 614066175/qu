package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.infra.vo.PlanGroupTreeVO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>评估方案分组表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanGroupMapper extends BaseMapper<PlanGroup> {

    /**
     * 含批数据评估方案列表
     *
     * @param planGroupTreeVO 查询条件
     * @return
     */
    List<PlanGroupTreeVO> treeBatch(PlanGroupTreeVO planGroupTreeVO);

    /**
     * 含实时数据评估方案列表
     *
     * @param planGroupTreeVO 查询条件
     * @return
     */
    List<PlanGroupTreeVO> treeStream(PlanGroupTreeVO planGroupTreeVO);
}
