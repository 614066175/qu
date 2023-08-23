package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StreamingResultDTO;
import org.xdsp.quality.api.dto.TimeRangeDTO;
import org.xdsp.quality.domain.entity.StreamingResult;
import org.xdsp.quality.domain.repository.StreamingResultRepository;
import org.xdsp.quality.infra.mapper.StreamingResultMapper;
import org.xdsp.quality.infra.util.TimeToString;
import org.xdsp.quality.infra.vo.MarkTrendVO;
import org.xdsp.quality.infra.vo.RuleExceptionVO;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.util.List;
import java.util.Map;

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
        return PageHelper.doPageAndSort(pageRequest, () -> streamingResultMapper.listByGroup(streamingResultDTO));
    }

    @Override
    public StreamingResultDTO showResultHead(StreamingResultDTO streamingResultDTO) {
        return streamingResultMapper.showResultHead(streamingResultDTO);
    }

    @Override
    public Page<StreamingResultDTO> listHistory(StreamingResultDTO streamingResultDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> streamingResultMapper.listHistory(streamingResultDTO));
    }

    @Override
    public Map<String, Object> numberView(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return streamingResultMapper.listResultMap(timeRangeDTO);
    }

    @Override
    public List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return streamingResultMapper.listMarkTrend(timeRangeDTO);
    }

    @Override
    public List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return streamingResultMapper.listWarningLevel(timeRangeDTO);
    }

    @Override
    public List<Map<String, Object>> delayTopicInfo(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return streamingResultMapper.listDelayTopic(timeRangeDTO);
    }

    @Override
    public List<RuleExceptionVO> ruleErrorTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return streamingResultMapper.listRuleError(timeRangeDTO);
    }
}
