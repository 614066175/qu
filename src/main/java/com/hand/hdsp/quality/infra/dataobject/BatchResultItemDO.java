package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.util.Date;

/**
 * <p>批数据方案结果表-校验项信息数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchResultItemDO extends AuditDomain {

    private Long resultItemId;

    private Long resultRuleId;

    private Long conditionId;

    private String checkWay;

    private String checkItem;

    private String countType;

    private String fieldName;

    private String checkFieldName;

    private String whereCondition;

    private String compareWay;

    private String regularExpression;

    private String lovCode;

    private String customSql;

    private String relDatasourceType;

    private Long relDatasourceId;

    private String relSchema;

    private String relTableName;

    private String relationship;

    private String warningLevelJson;

    private String actualValue;

    private String waveRate;

    private String currentValue;

    private String exceptionInfo;

    private String warningLevel;

    private Long tenantId;

    /**
     * 计算日期
     */
    private Date measureDate;
}
