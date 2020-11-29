package com.hand.hdsp.quality.infra.measure;

import java.util.HashMap;
import java.util.Map;

import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.base.BaseConstants;
import org.springframework.stereotype.Component;

/**
 * <p>校验项处理程序收集器</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
@Slf4j
public class CountCollector {
    private final LovAdapter lovAdapter;

    private static final Map<String, Count> COUNT_MAP = new HashMap<>();

    public CountCollector(LovAdapter lovAdapter) {
        this.lovAdapter = lovAdapter;
    }

    public void register(String countType, Count count) {
        if (COUNT_MAP.containsKey(countType)) {
            log.error("CountType {} exists", countType);
            throw new CommonException(ErrorCode.COUNT_TYPE_EXIST);
        }
        COUNT_MAP.put(countType, count);
    }

    public Count getCount(String countType) {
        Count count = COUNT_MAP.get(countType.toUpperCase());
        if (count == null) {
            String meaning = lovAdapter.queryLovMeaning(PlanConstant.LOV_COUNT_TYPE, BaseConstants.DEFAULT_TENANT_ID, countType);
            throw new CommonException(ErrorCode.COUNT_TYPE_NOT_EXIST, meaning);
        }
        return count;
    }

}
