package com.hand.hdsp.quality.infra.measure;

import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.exception.MessageException;
import org.hzero.core.message.MessageAccessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>校验项处理程序收集器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
@Slf4j
public class MeasureCollector {
    private final LovAdapter lovAdapter;

    private static final Map<String, Measure> MEASURE_MAP = new HashMap<>();

    public MeasureCollector(LovAdapter lovAdapter) {
        this.lovAdapter = lovAdapter;
    }

    public void register(String checkItem, Measure measure) {
        if (MEASURE_MAP.containsKey(checkItem)) {
            log.error("CheckItem {} exists", checkItem);
            throw new CommonException("error.measure.check_item.exist");
        }
        MEASURE_MAP.put(checkItem, measure);
    }

    public Measure getMeasure(String checkItem) {
        Measure measure = MEASURE_MAP.get(checkItem.toUpperCase());
        if (measure == null) {
            String meaning = lovAdapter.queryLovMeaning(PlanConstant.LOV_CHECK_ITEM, BaseConstants.DEFAULT_TENANT_ID, checkItem);
            Object[] args = {meaning};
            throw new MessageException(MessageAccessor.getMessage("error.measure.check_item.not_exist", args).getDesc());
        }
        return measure;
    }

}
