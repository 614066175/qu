package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>批数据方案结果表-校验项信息实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_batch_result_item")
public class BatchResultItem extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long resultItemId;

    private Long resultRuleId;

    private Long planLineId;

    private Long conditionId;

    private String checkWay;

    private String checkItem;

    private String countType;

    private String fieldName;

    private String checkFieldName;

    private String whereCondition;

    private String compareWay;

    private String regularExpression;

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

    private Long projectId;

    private Long nullNum;

    private Long uniqueNum;

    //表行数，是记录空值/总行数，指的是检验的表行数
    private Long tableLineNum;

}
