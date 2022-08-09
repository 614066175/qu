package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.PlanBaseAssign;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>质检项分配表Mapper</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
public interface PlanBaseAssignMapper extends BaseMapper<PlanBaseAssign> {

    /**
     * 删除方案或质检项的分配
     * @param planBaseIds
     * @param planId
     */
    void deleteAssignByPlan(@Param("planBaseIds") List<Long> planBaseIds, @Param("planId") Long planId);
}
