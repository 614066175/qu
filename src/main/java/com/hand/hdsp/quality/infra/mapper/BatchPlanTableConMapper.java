package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.BatchPlanTableCon;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableConDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>批数据方案-表级规则条件表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
public interface BatchPlanTableConMapper extends BaseMapper<BatchPlanTableCon> {

    List<BatchPlanTableConDO> selectJoinItem(BatchPlanTableConDO batchPlanFieldConDO);
}
