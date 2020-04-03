package com.hand.hdsp.quality.infra.mapper;

import java.util.List;
import java.util.Map;

import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.mybatis.common.BaseMapper;

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
     * 质量分析汇总:数据质量分数，规则总数，异常规则数
     *
     * @param timeRangeDTO
     * @return
     */
    Map<String,Object> listResultMap(TimeRangeDTO timeRangeDTO);


    /**
     * 查询时间范围内各个校验类型成功总数
     *
     * @param timeRangeDTO
     * @return CheckTypePercentageVO
     */
    List<CheckTypePercentageVO> listSUCCESSTypeCount(TimeRangeDTO timeRangeDTO);

    /**
     * 询时间范围内各个校验类型总数
     *
     * @param timeRangeDTO
     * @return CheckTypePercentageVO
     */
    List<CheckTypePercentageVO> listAllTypeCount(TimeRangeDTO timeRangeDTO);

    /**
     * 主要可改进指标:规则总数
     *
     * @param timeRangeDTO
     * @return RuleVO
     */
    List<RuleVO> listRule(TimeRangeDTO timeRangeDTO);

    /**
     * 主要可改进指标:异常规则数
     *
     * @param timeRangeDTO
     * @return RuleVO
     */
    List<RuleVO> listErrorRule(TimeRangeDTO timeRangeDTO);

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
}
