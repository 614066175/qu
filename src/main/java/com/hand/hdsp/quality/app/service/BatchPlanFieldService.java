package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>批数据方案-字段规则表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldService {

    /**
     * 删除（所有相关项）
     *
     * @param batchPlanFieldDTO 删除条件
     * @return 删除结果
     */
    int delete(BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 创建
     *
     * @param batchPlanFieldDTO
     */
    void insert(BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 更新
     *
     * @param batchPlanFieldDTO 更新规则信息
     */
    void update(BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 字段级规则明细
     *
     * @param planFieldId
     * @return
     */
    BatchPlanFieldDTO detail(Long planFieldId);

    /**
     * 字段级规则列表
     *
     * @param pageRequest
     * @param batchPlanFieldDTO
     * @return
     */
    Page<BatchPlanFieldDTO> list(PageRequest pageRequest, BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 字段级规则选择列表
     *
     * @param batchPlanFieldDTO
     * @param ruleModel
     * @param pageRequest
     * @return
     */
    Page<RuleDTO> select(BatchPlanFieldDTO batchPlanFieldDTO, String ruleModel, PageRequest pageRequest);
}
