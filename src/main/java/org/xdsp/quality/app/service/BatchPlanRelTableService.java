package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.quality.api.dto.BatchPlanRelTableDTO;

/**
 * <p>批数据方案-表间规则表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanRelTableService {

    /**
     * 表间规则明细
     *
     * @param planRuleId
     * @return
     */
    BatchPlanRelTableDTO detail(Long planRuleId);

    /**
     * 创建
     *
     * @param batchPlanRelTableDTO
     */
    void insert(BatchPlanRelTableDTO batchPlanRelTableDTO);

    /**
     * 更新
     *
     * @param batchPlanRelTableDTO 更新规则信息
     */
    void update(BatchPlanRelTableDTO batchPlanRelTableDTO);

    /**
     * 查询
     *
     * @param pageRequest
     * @param batchPlanRelTableDTO
     * @return
     */
    Page<BatchPlanRelTableDTO> list(PageRequest pageRequest, BatchPlanRelTableDTO batchPlanRelTableDTO);

    /**
     * 表间规则-规则详情查询方法
     *
     * @param pageRequest
     * @param batchPlanRelTableDTO
     * @return
     */
    Page<BatchPlanRelTableDTO> selectDetailList(PageRequest pageRequest, BatchPlanRelTableDTO batchPlanRelTableDTO);
}
