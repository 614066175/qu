package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.util.TimeToString;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return batchResultMapper.listResultHead(batchResultDTO);
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
        List<CheckTypePercentageVO> success = batchResultMapper.listSuccessTypeCount(timeRangeDTO);
        List<CheckTypePercentageVO> all = batchResultMapper.listAllTypeCount(timeRangeDTO);
        List<CheckTypePercentageVO> checkTypePercentage = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(all)) {
            Map<String, Long> allMap = all.stream().collect(Collectors.toMap(CheckTypePercentageVO::getCheckType, CheckTypePercentageVO::getCountSum));
            Map<String, Long> successMap = success.stream().collect(Collectors.toMap(CheckTypePercentageVO::getCheckType, CheckTypePercentageVO::getCountSum));
            allMap.forEach((checkType, countSum) -> {
                Long successSum = successMap.get(checkType) == null ? 0L : successMap.get(checkType);
                checkTypePercentage.add(CheckTypePercentageVO
                        .builder()
                        .checkType(checkType)
                        .percentage(Double.valueOf(new DecimalFormat("#.00")
                                .format((successSum * 1.0) / (countSum == 0 ? 1 : countSum))))
                        .build());
            });
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
    public List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return batchResultMapper.listMarkTrend(timeRangeDTO);
    }

    @Override
    public List<RuleExceptionVO> daysErrorRule(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return batchResultMapper.listRuleException(timeRangeDTO);
    }

    @Override
    public List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return batchResultMapper.listWarningLevel(timeRangeDTO);
    }

    @Override
    public Page<ErrorTableListVO> errorTableList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return PageHelper.doPage(pageRequest, () -> batchResultMapper.errorTableList(timeRangeDTO));
    }

    @Override
    public Page<ErrorTableItemListVO> errorTableItemList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        Page<ErrorTableItemListVO> pages = PageHelper.doPage(pageRequest, () -> batchResultMapper.errorTableItemList(timeRangeDTO));
        for (ErrorTableItemListVO dto : pages.getContent()) {
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevelJson()));
        }
        return pages;
    }

    @Override
    public List<BatchPlanFieldDTO> ruleList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return PageHelper.doPage(pageRequest, () -> batchResultMapper.ruleList(timeRangeDTO));
    }

    @Override
    public List<BatchPlanFieldDTO> itemList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        return PageHelper.doPage(pageRequest, () -> batchResultMapper.itemList(timeRangeDTO));
    }

    @Override
    public Page<BatchResultItemDTO> errorRuleList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        Page<BatchResultItemDTO> page = PageHelper.doPage(pageRequest, () -> batchResultMapper.errorRuleList(timeRangeDTO));
        for (BatchResultItemDTO dto : page.getContent()) {
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevelJson()));
        }
        return page;
    }

}
