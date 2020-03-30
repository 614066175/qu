package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>批数据方案-表级规则表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableMapper extends BaseMapper<BatchPlanTable> {

    /**
     * 表级规则列表
     *
     * @param batchPlanTableDO 查询条件
     * @return
     */
    List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO);
}
