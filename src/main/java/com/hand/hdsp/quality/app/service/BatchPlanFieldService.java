package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;
import java.util.Map;

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
     * @param planRuleId
     * @return
     */
    BatchPlanFieldDTO detail(Long planRuleId);

    /**
     * 已选字段规则列表
     *
     * @param batchPlanFieldDTO
     * @return
     */
    Map<String, List<BatchPlanFieldDTO>> listSelected(BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 可选标准规则详情（将标准规则转成字段规则的结构）
     *
     * @param ruleId
     * @return
     */
    BatchPlanFieldDTO selectDetail(Long ruleId);

    /**
     * 字段规则-规则详情查询方法
     *
     * @param pageRequest
     * @param batchPlanFieldDTO
     * @return
     */
    Page<BatchPlanFieldDTO> selectDetailList(PageRequest pageRequest, BatchPlanFieldDTO batchPlanFieldDTO);
}
