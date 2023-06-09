package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.StreamingResultDTO;
import org.xdsp.quality.api.dto.TimeRangeDTO;
import org.xdsp.quality.domain.entity.StreamingResult;
import org.xdsp.quality.infra.vo.MarkTrendVO;
import org.xdsp.quality.infra.vo.RuleExceptionVO;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.util.List;
import java.util.Map;

/**
 * <p>实时数据方案结果表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultMapper extends BaseMapper<StreamingResult> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param streamingResultDTO
     * @return StreamingResultDTO
     */
    List<StreamingResultDTO> listByGroup(StreamingResultDTO streamingResultDTO);

    /**
     * 查看评估方案的评估结果头
     *
     * @param streamingResultDTO
     * @return StreamingResultDTO
     */
    StreamingResultDTO showResultHead(StreamingResultDTO streamingResultDTO);

    /**
     *  根据分组查询对应的评估方案执行记录
     *
     * @param streamingResultDTO
     * @return StreamingResultDTO
     */
    List<StreamingResultDTO> listHistory(StreamingResultDTO streamingResultDTO);

    /**
     * 质量分析汇总:数据质量分数，规则总数，异常规则数
     *
     * @param timeRangeDTO
     * @return
     */
    Map<String,Object> listResultMap(TimeRangeDTO timeRangeDTO);

    /**
     * 每日分数走势
     *
     * @param timeRangeDTO
     * @return MarkTrendVO
     */
    List<MarkTrendVO> listMarkTrend(TimeRangeDTO timeRangeDTO);

    /**
     * 展示不同等级每日告警数
     *
     * @param timeRangeDTO
     * @return WarningLevelVO
     */
    List<WarningLevelVO> listWarningLevel(TimeRangeDTO timeRangeDTO);

    /**
     * 查询topic延迟
     *
     * @param timeRangeDTO
     * @return
     */
    List<Map<String,Object>> listDelayTopic(TimeRangeDTO timeRangeDTO);

    /**
     * 展示不同类型异常数
     *
     * @param timeRangeDTO
     * @return RuleExceptionVO
     */
    List<RuleExceptionVO> listRuleError(TimeRangeDTO timeRangeDTO);
}
