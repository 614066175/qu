package com.hand.hdsp.quality.domain.repository;

import java.util.List;
import java.util.Map;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import com.hand.hdsp.quality.infra.vo.MarkTrendVO;
import com.hand.hdsp.quality.infra.vo.RuleExceptionVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>实时数据方案结果表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultRepository extends BaseRepository<StreamingResult, StreamingResultDTO>, ProxySelf<StreamingResultRepository> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param streamingResultDTO
     * @param pageRequest
     * @return StreamingResultDTO
     */
    Page<StreamingResultDTO> listAll(StreamingResultDTO streamingResultDTO, PageRequest pageRequest);

    /**
     * 查看评估方案的评估结果头
     *
     * @param streamingResultDTO
     * @return
     */
    StreamingResultDTO showResultHead(StreamingResultDTO streamingResultDTO);

    /**
     * 根据分组查询对应的评估方案执行记录
     *
     * @param streamingResultDTO
     * @param pageRequest
     * @return StreamingResultDTO
     */
    Page<StreamingResultDTO> listHistory(StreamingResultDTO streamingResultDTO, PageRequest pageRequest);

    /**
     * 查看质量分数，规则总数，异常规则数
     *
     * @param timeRangeDTO
     * @return
     */
    Map<String,Object> numberView(TimeRangeDTO timeRangeDTO);

    /**
     * 数据质量分数走势
     *
     * @param timeRangeDTO
     * @return MarkTrendVO
     */
    List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO);

    /**
     * 不同告警等级趋势
     *
     * @param timeRangeDTO
     * @return WarningLevelVO
     */
    List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO);

    /**
     * topic延迟
     *
     * @param timeRangeDTO
     * @return
     */
    List<Map<String,Object>> delayTopicInfo(TimeRangeDTO timeRangeDTO);

    /**
     * 展示不同类型异常数
     *
     * @param timeRangeDTO
     * @return RuleExceptionVO
     */
    List<RuleExceptionVO> ruleErrorTrend(TimeRangeDTO timeRangeDTO);
}
