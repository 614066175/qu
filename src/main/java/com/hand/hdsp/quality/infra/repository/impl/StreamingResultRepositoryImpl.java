package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;
import java.util.Map;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import com.hand.hdsp.quality.domain.repository.StreamingResultRepository;
import com.hand.hdsp.quality.infra.mapper.StreamingResultMapper;
import com.hand.hdsp.quality.infra.util.TimeToString;
import com.hand.hdsp.quality.infra.vo.MarkTrendVO;
import com.hand.hdsp.quality.infra.vo.RuleExceptionVO;
import com.hand.hdsp.quality.infra.vo.StringTimeVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>实时数据方案结果表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultRepositoryImpl extends BaseRepositoryImpl<StreamingResult, StreamingResultDTO> implements StreamingResultRepository {

    private final StreamingResultMapper streamingResultMapper;

    public StreamingResultRepositoryImpl(StreamingResultMapper streamingResultMapper) {
        this.streamingResultMapper = streamingResultMapper;
    }

    @Override
    public Page<StreamingResultDTO> listAll(StreamingResultDTO streamingResultDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,() -> streamingResultMapper.listByGroup(streamingResultDTO));
    }

    @Override
    public StreamingResultDTO showResultHead(StreamingResultDTO streamingResultDTO) {
        StreamingResultDTO resultDTO = streamingResultMapper.showResultHead(streamingResultDTO);
        return resultDTO;
    }

    @Override
    public Page<StreamingResultDTO> listHistory(StreamingResultDTO streamingResultDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()-> streamingResultMapper.listHistory(streamingResultDTO));
    }

    @Override
    public Map<String, Object> numberView(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        Map<String, Object> resultMap = streamingResultMapper.listResultMap(timeRangeDTO);
        return resultMap;
    }

    @Override
    public List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<MarkTrendVO> markTrendVOS = streamingResultMapper.listMarkTrend(timeRangeDTO);
        return markTrendVOS;
    }

    @Override
    public List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<WarningLevelVO> warningLevelVOS = streamingResultMapper.listWarningLevel(timeRangeDTO);
        return warningLevelVOS;
    }

    @Override
    public List<Map<String, Object>> delayTopicInfo(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<Map<String, Object>> listDelayTopic = streamingResultMapper.listDelayTopic(timeRangeDTO);
        return listDelayTopic;
    }

    @Override
    public List<RuleExceptionVO> ruleErrorTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<RuleExceptionVO> ruleExceptionVOS = streamingResultMapper.listRuleError(timeRangeDTO);
        return ruleExceptionVOS;
    }
}
