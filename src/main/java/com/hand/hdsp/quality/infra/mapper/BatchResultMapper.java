package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>批数据方案结果表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface BatchResultMapper extends BaseMapper<BatchResult> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    List<BatchResultDTO> listByGroup(BatchResultDTO batchResultDTO);

    /**
     * 查看评估方案的评估结果头
     *
     * @param batchResultDTO
     * @return
     */
    BatchResultDTO listResultHead(BatchResultDTO batchResultDTO);

    /**
     * 根据分组查询对应的评估方案执行记录
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    List<BatchResultDTO> listHistory(BatchResultDTO batchResultDTO);

    /**
     * 质量分析汇总:数据质量分数
     *
     * @param timeRangeDTO
     * @return
     */
    long selectScore(TimeRangeDTO timeRangeDTO);

    /**
     * 质量分析汇总:，规则总数
     *
     * @param timeRangeDTO
     * @return
     */
    long selectRuleCount(TimeRangeDTO timeRangeDTO);

    /**
     * 质量分析汇总:告警次数
     *
     * @param timeRangeDTO
     * @return
     */
    long selectWarningCount(TimeRangeDTO timeRangeDTO);


    /**
     * 查询时间范围内各个校验类型成功总数
     *
     * @param timeRangeDTO
     * @return CheckTypePercentageVO
     */
    List<CheckTypePercentageVO> listSuccessTypeCount(TimeRangeDTO timeRangeDTO);

    /**
     * 询时间范围内各个校验类型总数
     *
     * @param timeRangeDTO
     * @return CheckTypePercentageVO
     */
    List<CheckTypePercentageVO> listAllTypeCount(TimeRangeDTO timeRangeDTO);

    /**
     * 每日分数走势
     *
     * @param timeRangeDTO
     * @return MarkTrendVO
     */
    List<MarkTrendVO> listMarkTrend(TimeRangeDTO timeRangeDTO);

    /**
     * 展示不同类型异常数
     *
     * @param timeRangeDTO
     * @return RuleExceptionVO
     */
    List<RuleExceptionVO> listRuleException(TimeRangeDTO timeRangeDTO);

    /**
     * 展示不同等级每日告警数
     *
     * @param timeRangeDTO
     * @return WarningLevelVO
     */
    List<WarningLevelVO> listWarningLevel(TimeRangeDTO timeRangeDTO);

    /**
     * 灾区表占比情况
     *
     * @param timeRangeDTO
     * @return
     */
    List<ErrorTablePercentageVO> errorTablePercentage(TimeRangeDTO timeRangeDTO);

    /**
     * 问题触发次数统计
     * @param timeRangeDTO 时间查询条件
     * @return 返回值
     */
    List<ProblemTriggerVO> problemTrigger(TimeRangeDTO timeRangeDTO);

    /**
     * 灾区表-校验项占比情况
     *
     * @param timeRangeDTO
     * @return
     */
    List<ErrorTablePercentageVO> errorTableItemPercentage(TimeRangeDTO timeRangeDTO);

    /**
     * 灾区表列表
     *
     * @param timeRangeDTO
     * @return
     */
    List<ErrorTableListVO> errorTableList(TimeRangeDTO timeRangeDTO);

    /**
     * 灾区表-校验项列表
     *
     * @param timeRangeDTO
     * @return
     */
    List<ErrorTableItemListVO> errorTableItemList(TimeRangeDTO timeRangeDTO);

    /**
     * 规则列表
     *
     * @param timeRangeDTO
     * @return
     */
    List<BatchPlanFieldDTO> ruleList(TimeRangeDTO timeRangeDTO);

    /**
     * 校验项列表
     *
     * @param timeRangeDTO
     * @return
     */
    List<BatchPlanFieldDTO> itemList(TimeRangeDTO timeRangeDTO);

    /**
     * 告警列表
     *
     * @param timeRangeDTO
     * @return
     */
    List<BatchResultItemDTO> errorRuleList(TimeRangeDTO timeRangeDTO);

    /**
     * 根据planId查询最新的执行结果
     *
     * @param planId
     * @return
     */
    BatchResultDTO selectByPlanId(@Param("planId") Long planId);

    /**
     * @param batchResultDTO
     * @return
     */
    List<BatchResultMarkDTO> listResultDetail(BatchResultDTO batchResultDTO);

    /**
     * 当方案是全量同步时查询问题趋势
     * @param timeRangeDTO 查询条件
     * @return 返回值
     */
    List<TimeRangeDTO> selectProblemTrend(TimeRangeDTO timeRangeDTO);

    /**
     * 当方案是增量同步时查询问题趋势
     * @param timeRangeDTO 查询条件
     * @return 返回值
     */
    List<TimeRangeDTO> selectProblemWithIncre(TimeRangeDTO timeRangeDTO);

    /**
     * 删除结果
     * @param deleteResultIds
     */
    void deleteResult(List<Long> deleteResultIds);

    /**
     * 删除质检项结果
     * @param deleteResultIds
     */
    void deleteResultBase(List<Long> deleteResultIds);

    /**
     * 删除规则结果
     * @param deleteResultIds
     */
    void deleteResultRule(List<Long> deleteResultIds);

    /**
     * 删除校验项结果
     * @param deleteResultIds
     */
    void deleteResultItem(List<Long> deleteResultIds);
}
