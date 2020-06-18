package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.util.TimeToString;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * <p>批数据方案结果表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class BatchResultRepositoryImpl extends BaseRepositoryImpl<BatchResult, BatchResultDTO> implements BatchResultRepository {

    private final BatchResultMapper batchResultMapper;

    public BatchResultRepositoryImpl(BatchResultMapper batchResultMapper) {
        this.batchResultMapper = batchResultMapper;
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
        return PageHelper.doPageAndSort(pageRequest, () -> batchResultMapper.listHistory(batchResultDTO));
    }


    @Override
    public Map<String, Object> numberView(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        Map<String, Object> map = new HashMap<>(8);
        map.put("mark", batchResultMapper.selectScore(timeRangeDTO));
        map.put("ruleCount", batchResultMapper.selectRuleCount(timeRangeDTO));
        map.put("exceptionRuleCount", batchResultMapper.selectWarningCount(timeRangeDTO));
        return map;
    }

    @Override
    public List<CheckTypePercentageVO> checkTypePercentage(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<CheckTypePercentageVO> success = batchResultMapper.listSUCCESSTypeCount(timeRangeDTO);
        List<CheckTypePercentageVO> all = batchResultMapper.listAllTypeCount(timeRangeDTO);
        List<CheckTypePercentageVO> checkTypePercentage = new ArrayList<>();
        if (!success.isEmpty() && !all.isEmpty()) {
            for (CheckTypePercentageVO a : all) {
                for (CheckTypePercentageVO s : success) {
                    if (a.getCheckType().equals(s.getCheckType())) {
                        checkTypePercentage.add(CheckTypePercentageVO
                                .builder()
                                .checkType(a.getCheckType())
                                .percentage(Double.valueOf(new DecimalFormat("#.00").format((s.getCountSum() * 1.0) / (a.getCountSum() == 0 ? 1 : a.getCountSum()))))
                                .build());
                    }
                }
            }
        } else {
            return Collections.singletonList(new CheckTypePercentageVO());
        }
        return checkTypePercentage;
    }

    @Override
    public List<ErrorTablePercentageVO> errorTablePercentage(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<ErrorTablePercentageVO> list = batchResultMapper.errorTablePercentage(timeRangeDTO);
        BigDecimal sum = list.stream().map(ErrorTablePercentageVO::getCountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        list.forEach(vo -> vo.setPercentage(vo.getCountSum().divide(sum, 2, RoundingMode.HALF_UP)));
        return list;
    }

    @Override
    public List<ErrorTablePercentageVO> errorTableItemPercentage(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<ErrorTablePercentageVO> list = batchResultMapper.errorTableItemPercentage(timeRangeDTO);
        BigDecimal sum = list.stream().map(ErrorTablePercentageVO::getCountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        list.forEach(vo -> vo.setPercentage(vo.getCountSum().divide(sum, 2, RoundingMode.HALF_UP)));
        return list;
    }

    @Override
    public List<Map.Entry<String, Double>> rulePercentage(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<RuleVO> ruleVOS = batchResultMapper.listRule(timeRangeDTO);
        List<RuleVO> listErrorRule = batchResultMapper.listErrorRule(timeRangeDTO);
        HashMap<String, Double> ruleMap = new HashMap<>(10);
        if (!ruleVOS.isEmpty() && !listErrorRule.isEmpty()) {
            for (RuleVO ruleVO : ruleVOS) {
                for (RuleVO errorRuleVO : listErrorRule) {
                    if (ruleVO.getRuleId().equals(errorRuleVO.getRuleId())) {
                        ruleMap.put(Optional.ofNullable(errorRuleVO.getRuleName()).orElse(""), errorRuleVO.getCountSum() * 1.0 / (ruleVO.getCountSum() == 0 ? 1 : ruleVO.getCountSum()));
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
        TimeToString.timeToString(timeRangeDTO);
        List<MarkTrendVO> markTrendVOS = batchResultMapper.listMarkTrend(timeRangeDTO);
        return markTrendVOS;
    }

    @Override
    public List<RuleExceptionVO> daysErrorRule(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<RuleExceptionVO> ruleExceptionVOS = batchResultMapper.listRuleException(timeRangeDTO);
        return ruleExceptionVOS;
    }

    @Override
    public List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        List<WarningLevelVO> warningLevelVOS = batchResultMapper.listWarningLevel(timeRangeDTO);
        return warningLevelVOS;
    }

}
