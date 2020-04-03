package com.hand.hdsp.quality.infra.repository.impl;

import static java.util.Map.Entry.comparingByValue;
import java.util.*;
import java.util.stream.Collectors;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultRuleRepository;
import com.hand.hdsp.quality.infra.constant.WarnLevel;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.util.TimeToString;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class BatchResultRepositoryImpl extends BaseRepositoryImpl<BatchResult, BatchResultDTO> implements BatchResultRepository {

    private final BatchResultMapper batchResultMapper;
    private final BatchPlanRepository batchPlanRepository;
    private final BatchResultBaseRepository batchResultBaseRepository;
    private final BatchResultRuleRepository batchResultRuleRepository;

    public BatchResultRepositoryImpl(BatchResultMapper batchResultMapper, BatchPlanRepository batchPlanRepository, BatchResultBaseRepository batchResultBaseRepository, BatchResultRuleRepository batchResultRuleRepository) {
        this.batchResultMapper = batchResultMapper;
        this.batchPlanRepository = batchPlanRepository;
        this.batchResultBaseRepository = batchResultBaseRepository;
        this.batchResultRuleRepository = batchResultRuleRepository;
    }

    @Override
    public Page<BatchResultDTO> listAll(BatchResultDTO batchResultDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> batchResultMapper.listByGroup(batchResultDTO));
    }

    @Override
    public BatchResultDTO showResultHead(BatchResultDTO batchResultDTO) {
        BatchResultDTO batchResult = batchResultMapper.listResultHead(batchResultDTO);
        return batchResult;
    }

    @Override
    public Page<BatchResultDTO> listHistory(BatchResultDTO batchResultDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()-> batchResultMapper.listHistory(batchResultDTO));
    }


    @Override
    public Map<String, Object> numberView(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        Map<String, Object> resultMap = batchResultMapper.listResultMap(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return resultMap;
    }

    @Override
    public List<CheckTypePercentageVO> checkTypePercentage(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        List<CheckTypePercentageVO> success = batchResultMapper.listSUCCESSTypeCount(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        List<CheckTypePercentageVO> all = batchResultMapper.listAllTypeCount(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        List<CheckTypePercentageVO> checkTypePercentage = new ArrayList<>();
        if (!success.isEmpty() && !all.isEmpty()){
            for (CheckTypePercentageVO a : all){
                for (CheckTypePercentageVO s : success){
                    if (a.getCheckType().equals(s.getCheckType())){
                        checkTypePercentage.add(CheckTypePercentageVO
                                .builder()
                                .checkType(a.getCheckType())
                                .percentage((s.getCountSum()*1.0)/(a.getCountSum() == 0 ? 1 : a.getCountSum()))
                                .build());
                    }
                }
            }
        } else {
            return Arrays.asList(new CheckTypePercentageVO());
        }
        return checkTypePercentage;
    }

    @Override
    public List<Map.Entry<String, Double>> rulePercentage(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        List<RuleVO> ruleVOS = batchResultMapper.listRule(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        List<RuleVO> listErrorRule = batchResultMapper.listErrorRule(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        HashMap<String, Double> ruleMap = new HashMap<>();
        if (!ruleVOS.isEmpty() && !listErrorRule.isEmpty()){
            for (RuleVO ruleVO : ruleVOS){
                for (RuleVO errorRuleVO : listErrorRule){
                    if (ruleVO.getRuleId().equals(errorRuleVO.getRuleId())){
                        ruleMap.put(errorRuleVO.getRuleName(),errorRuleVO.getCountSum()*1.0/(ruleVO.getCountSum() == 0 ? 1 : ruleVO.getCountSum()));
                    }
                }
            }
        } else {
            return Collections.emptyList();
        }
        List<Map.Entry<String, Double>> entryList = ruleMap.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(Collectors.toList());
        return entryList;
    }

    @Override
    public List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        List<MarkTrendVO> markTrendVOS = batchResultMapper.listMarkTrend(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return markTrendVOS;
    }

    @Override
    public List<RuleExceptionVO> daysErrorRule(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(),timeRangeDTO.getStart(),timeRangeDTO.getEnd());
        List<RuleExceptionVO> ruleExceptionVOS = batchResultMapper.listRuleException(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return ruleExceptionVOS;
    }

    @Override
    public List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO) {
        StringTimeVO stringTimeVO = TimeToString.timeToSring(timeRangeDTO.getTimeRange(), timeRangeDTO.getStart(), timeRangeDTO.getEnd());
        List<WarningLevelVO> warningLevelVOS = batchResultMapper.listWarningLevel(TimeRangeDTO
                .builder()
                .tenantId(timeRangeDTO.getTenantId())
                .startDate(stringTimeVO.getStart())
                .endDate(stringTimeVO.getEnd())
                .build()
        );
        return warningLevelVOS;
    }


}
