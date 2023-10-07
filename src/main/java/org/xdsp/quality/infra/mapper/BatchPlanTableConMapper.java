package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.domain.entity.BatchPlanTableCon;
import org.xdsp.quality.infra.dataobject.BatchPlanTableConDO;

import java.util.List;

/**
 * <p>批数据方案-表级规则条件表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
public interface BatchPlanTableConMapper extends BaseMapper<BatchPlanTableCon> {

    /**
     * 关联查询校验项
     *
     * @param batchPlanFieldConDO
     * @return
     */
    List<BatchPlanTableConDO> selectJoinItem(BatchPlanTableConDO batchPlanFieldConDO);

    /**
     * 根据 planBaseId 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);
}
