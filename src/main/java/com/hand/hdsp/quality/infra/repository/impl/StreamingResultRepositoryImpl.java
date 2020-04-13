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
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        Map<String, Object> resultMap = streamingResultMapper.listResultMap(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return resultMap;
    }

    @Override
    public List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        List<MarkTrendVO> markTrendVOS = streamingResultMapper.listMarkTrend(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return markTrendVOS;
    }

    @Override
    public List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        List<WarningLevelVO> warningLevelVOS = streamingResultMapper.listWarningLevel(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return warningLevelVOS;
    }

    @Override
    public List<Map<String, Object>> delayTopicInfo(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        List<Map<String, Object>> listDelayTopic = streamingResultMapper.listDelayTopic(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return listDelayTopic;
    }

    @Override
    public List<RuleExceptionVO> ruleErrorTrend(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(),timeRangeDTO.getStart(),timeRangeDTO.getEnd());
        List<RuleExceptionVO> ruleExceptionVOS = streamingResultMapper.listRuleError(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return ruleExceptionVOS;
    }
}
