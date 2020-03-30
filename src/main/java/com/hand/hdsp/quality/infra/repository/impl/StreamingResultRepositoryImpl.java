package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import com.hand.hdsp.quality.domain.entity.StreamingResultBase;
import com.hand.hdsp.quality.domain.entity.StreamingResultRule;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRepository;
import com.hand.hdsp.quality.domain.repository.StreamingResultBaseRepository;
import com.hand.hdsp.quality.domain.repository.StreamingResultRepository;
import com.hand.hdsp.quality.domain.repository.StreamingResultRuleRepository;
import com.hand.hdsp.quality.infra.mapper.StreamingResultMapper;
import io.choerodon.core.domain.Page;
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
    public StreamingResultDTO showReport(StreamingResultDTO streamingResultDTO) {
        StreamingResultDTO streamingResultDTOs = this.selectDTOByCondition(
                Condition.builder(StreamingResult.class)
                        .where(Sqls.custom()
                                .andEqualTo(StreamingResult.FIELD_PLAN_ID, streamingResultDTO.getPlanId(), true)
                                .andEqualTo(StreamingResult.FIELD_TENANT_ID, streamingResultDTO.getTableId(), true))
                        .build()).get(0);
        streamingResultDTOs.setPlanName(streamingPlanRepository.selectByPrimaryKey(streamingResultDTO.getPlanId()).getPlanName());
        List<StreamingResultBase> streamingResultBases = streamingResultBaseRepository.selectByCondition(
                Condition.builder(StreamingResultBase.class)
                        .where(Sqls.custom()
                                .andEqualTo(StreamingResultBase.FIELD_RESULT_ID, streamingResultDTOs.getResultId(), true))
                        .build()
        );
        List<Long> resultBaseIds = streamingResultBases.stream().map(StreamingResultBase::getResultBaseId).collect(Collectors.toList());
        List<StreamingResultRule> streamingResultRules = streamingResultRuleRepository.selectByCondition(
                Condition.builder(StreamingResultRule.class)
                        .where(Sqls.custom()
                                .andIn(StreamingResultRule.FIELD_RESULT_BASE_ID, resultBaseIds))
                        .build()
        );
        streamingResultDTOs.setStreamingResultBases(streamingResultBases);
        streamingResultDTOs.setStreamingResultRules(streamingResultRules);
        return streamingResultDTOs;
    }
}
