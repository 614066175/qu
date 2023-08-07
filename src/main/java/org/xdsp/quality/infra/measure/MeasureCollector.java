package org.xdsp.quality.infra.measure;

import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.mapper.ItemTemplateSqlMapper;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.base.BaseConstants;
import org.springframework.stereotype.Component;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.domain.repository.ItemTemplateSqlRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.PlanConstant;

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
    private final ItemTemplateSqlRepository itemTemplateSqlRepository;
    private final ItemTemplateSqlMapper itemTemplateSqlMapper;

    private static final Map<String, Measure> MEASURE_MAP = new HashMap<>();

    public MeasureCollector(LovAdapter lovAdapter, ItemTemplateSqlRepository itemTemplateSqlRepository, ItemTemplateSqlMapper itemTemplateSqlMapper) {
        this.lovAdapter = lovAdapter;
        this.itemTemplateSqlRepository = itemTemplateSqlRepository;
        this.itemTemplateSqlMapper = itemTemplateSqlMapper;
    }

    public void register(String checkItem, Measure measure) {
        if (MEASURE_MAP.containsKey(checkItem)) {
            log.error("CheckItem {} exists", checkItem);
            throw new CommonException(ErrorCode.CHECK_ITEM_EXIST);
        }
        MEASURE_MAP.put(checkItem, measure);
    }

    public Measure getMeasure(String checkItem) {

        Measure measure = MEASURE_MAP.get(checkItem.toUpperCase());

        if (measure == null) {
            //判断有没有检验sql，有的话走通用sql检验器逻辑
            List<ItemTemplateSql> itemTemplateSqls =
                    itemTemplateSqlMapper.selectSql(ItemTemplateSql.builder().checkItem(checkItem).build());
            if (CollectionUtils.isNotEmpty(itemTemplateSqls)) {
                measure = MEASURE_MAP.get(PlanConstant.COMMON_SQL);
            }
        }
        if (measure == null) {
            String meaning = lovAdapter.queryLovMeaning(PlanConstant.LOV_CHECK_ITEM, BaseConstants.DEFAULT_TENANT_ID, checkItem);
            throw new CommonException(ErrorCode.CHECK_ITEM_NOT_EXIST, meaning);
        }
        return measure;
    }

}
