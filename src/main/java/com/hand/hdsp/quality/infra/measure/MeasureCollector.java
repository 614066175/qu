package com.hand.hdsp.quality.infra.measure;

import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private ItemTemplateSqlRepository itemTemplateSqlRepository;

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
            List<ItemTemplateSql> list = itemTemplateSqlRepository.select(ItemTemplateSql.builder().checkItem(checkItem).build());
            if (CollectionUtils.isNotEmpty(list)) {
                measure = MEASURE_MAP.get(PlanConstant.COMMON_SQL);
            }
        }
        if (measure == null) {
            String meaning = lovAdapter.queryLovMeaning(PlanConstant.LOV_CHECK_ITEM, BaseConstants.DEFAULT_TENANT_ID, checkItem);
            throw new CommonException("error.measure.check_item.not_exist", meaning);
        }
        return measure;
    }

}
