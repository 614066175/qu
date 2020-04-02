package com.hand.hdsp.quality.infra.repository.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingResultBaseDTO;
import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.api.dto.StreamingResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import com.hand.hdsp.quality.domain.entity.StreamingResultBase;
import com.hand.hdsp.quality.domain.entity.StreamingResultRule;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRepository;
import com.hand.hdsp.quality.domain.repository.StreamingResultBaseRepository;
import com.hand.hdsp.quality.domain.repository.StreamingResultRepository;
import com.hand.hdsp.quality.domain.repository.StreamingResultRuleRepository;
import com.hand.hdsp.quality.infra.constant.WarnLevel;
import com.hand.hdsp.quality.infra.mapper.StreamingResultMapper;
import com.hand.hdsp.quality.infra.util.TimeToString;
import com.hand.hdsp.quality.infra.vo.MarkTrendVO;
import com.hand.hdsp.quality.infra.vo.RuleExceptionVO;
import com.hand.hdsp.quality.infra.vo.StringTimeVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>实时数据方案结果表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultRepositoryImpl extends BaseRepositoryImpl<StreamingResult, StreamingResultDTO> implements StreamingResultRepository {

    private final StreamingResultMapper streamingResultMapper;
    private final StreamingPlanRepository streamingPlanRepository;
    private final StreamingResultBaseRepository streamingResultBaseRepository;
    private final StreamingResultRuleRepository streamingResultRuleRepository;

    public StreamingResultRepositoryImpl(StreamingResultMapper streamingResultMapper, StreamingPlanRepository streamingPlanRepository, StreamingResultBaseRepository streamingResultBaseRepository, StreamingResultRuleRepository streamingResultRuleRepository) {
        this.streamingResultMapper = streamingResultMapper;
        this.streamingPlanRepository = streamingPlanRepository;
        this.streamingResultBaseRepository = streamingResultBaseRepository;
        this.streamingResultRuleRepository = streamingResultRuleRepository;
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
    public Map<String, Object> numberView(Long tenantId, String timeRange, Date startDate, Date endDate) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRange, startDate, endDate);
        Map<String, Object> resultMap = streamingResultMapper.listResultMap(tenantId, stringTimeVO.getStart(), stringTimeVO.getEnd());
        return resultMap;
    }

    @Override
    public List<MarkTrendVO> markTrend(Long tenantId, String timeRange, Date startDate, Date endDate) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRange, startDate, endDate);
        List<MarkTrendVO> markTrendVOS = streamingResultMapper.listMarkTrend(tenantId, stringTimeVO.getStart(), stringTimeVO.getEnd());
        return markTrendVOS;
    }

    @Override
    public List<WarningLevelVO> warningTrend(Long tenantId, String timeRange, Date startDate, Date endDate) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRange, startDate, endDate);
        List<WarningLevelVO> warningLevelVOS = streamingResultMapper.listWarningLevel(tenantId, stringTimeVO.getStart(), stringTimeVO.getEnd());
        return warningLevelVOS;
    }

    @Override
    public List<Map<String, Object>> delayTopicInfo(Long tenantId, String timeRange, Date startDate, Date endDate, String topicInfo) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRange, startDate, endDate);
        List<Map<String, Object>> listDelayTopic = streamingResultMapper.listDelayTopic(tenantId, stringTimeVO.getStart(), stringTimeVO.getEnd(), topicInfo);
        return listDelayTopic;
    }

    @Override
    public List<RuleExceptionVO> ruleErrorTrend(Long tenantId, String timeRange, Date startDate, Date endDate) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRange, startDate, endDate);
        List<RuleExceptionVO> ruleExceptionVOS = streamingResultMapper.listRuleError(tenantId, stringTimeVO.getStart(), stringTimeVO.getEnd());
        return ruleExceptionVOS;
    }
}
