package com.hand.hdsp.quality.domain.repository;

import java.util.List;
import java.util.Map;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>批数据方案结果表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface BatchResultRepository extends BaseRepository<BatchResult, BatchResultDTO>, ProxySelf<BatchResultRepository> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param batchResultDTO
     * @param pageRequest
     * @return BatchResultDTO
     */
    Page<BatchResultDTO> listAll(BatchResultDTO batchResultDTO, PageRequest pageRequest);

    /**
     * 查看评估方案的评估结果头
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    BatchResultDTO showResultHead(BatchResultDTO batchResultDTO);

    /**
     * 根据分组查询对应的评估方案执行记录
     *
     * @param batchResultDTO
     * @param pageRequest
     * @return BatchResultDTO
     */
    Page<BatchResultDTO> listHistory(BatchResultDTO batchResultDTO, PageRequest pageRequest);

    /**
     * 查看质量分数，规则总数，异常规则数
     *
     * @param timeRangeDTO
     * @return
     */
    Map<String,Object> numberView(TimeRangeDTO timeRangeDTO);


    /**
     * 查看校验类型百分比
     *
     * @param timeRangeDTO
     * @return CheckTypePercentageVO
     */
    List<CheckTypePercentageVO> checkTypePercentage(TimeRangeDTO timeRangeDTO);

    /**
     * 主要可改进指标 规则百分比
     *
     * @param timeRangeDTO
     * @return
     */
    List<Map.Entry<String, Double>> rulePercentage(TimeRangeDTO timeRangeDTO);

    /**
     * 数据质量分数走势
     *
     * @param timeRangeDTO
     * @return MarkTrendVO
     */
    List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO);

    /**
     * 表级，表间，字段级异常规则数
     *
     * @param timeRangeDTO
     * @return RuleExceptionVO
     */
    List<RuleExceptionVO> daysErrorRule(TimeRangeDTO timeRangeDTO);

    /**
     * 不同告警等级趋势
     *
     * @param timeRangeDTO
     * @return WarningLevelVO
     */
    List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO);
}
