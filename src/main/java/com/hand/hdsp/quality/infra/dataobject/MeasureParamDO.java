package com.hand.hdsp.quality.infra.dataobject;


import java.util.List;
import java.util.Map;

import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import lombok.*;
import org.hzero.boot.driver.api.dto.PluginDatasourceDTO;

/**
 * <p>方案评估参数封装对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MeasureParamDO {

    private Long tenantId;

    private Long conditionId;

    private String fieldName;

    private String checkFieldName;

    /**
     * 校验项 HDSP.XQUA.CHECK_ITEM
     */
    private String checkItem;

    /**
     * 校验类型 HDSP.XQUA.COUNT_TYPE
     */
    private String countType;

    /**
     * 比较方式 HDSP.XQUA.COMPARE_WAY
     */
    private String compareWay;

    private String whereCondition;

    /**
     * 计算出来的值
     */
    private String countValue;

    private String warningLevel;

    private List<WarningLevelDTO> warningLevelList;

    private String regularExpression;

    private PluginDatasourceDTO pluginDatasourceDTO;

    private String sql;

    private String schema;

    private BatchResultBase batchResultBase;

    private BatchResultRuleDTO batchResultRuleDTO;

    private BatchResultItem batchResultItem;

    private BatchPlanRelTable batchPlanRelTable;

    private List<Map<String, Object>> exceptionMapList;

    private String ruleName;

}
