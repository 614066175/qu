package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.domain.entity.BatchPlanFieldCon;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanFieldConDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>批数据方案-字段规则条件表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
public interface BatchPlanFieldConMapper extends BaseMapper<BatchPlanFieldCon> {

    /**
     * 关联查询校验项
     *
     * @param batchPlanFieldConDO
     * @return
     */
    List<BatchPlanFieldConDO> selectJoinItem(BatchPlanFieldConDO batchPlanFieldConDO);

    /**
     * 根据 planBaseId 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);

    /**
     * 根据 planId 删除
     * @param planLineId
     * @return
     */
    int deleteByPlanLineId(@Param("planLineId") Long planLineId);
}
