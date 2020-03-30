package com.hand.hdsp.quality.infra.measure;

import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
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

    private static final Map<String, Measure> MEASURE_MAP = new HashMap<>();

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
            throw new CommonException("error.measure.check_item.not_exist");
        }
        return measure;
    }

}
