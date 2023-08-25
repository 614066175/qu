package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <p>批数据方案结果表-表信息实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_batch_result_base")
public class BatchResultBase extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long resultBaseId;

    private Long resultId;

    private Long planBaseId;

    private String objectName;

    private String incrementStrategy;

    private String incrementColumn;

    private String whereCondition;

    private Long ruleCount;

    private Long exceptionRuleCount;

    private Long checkItemCount;

    private Long exceptionCheckItemCount;

    private Long dataCount;

    private Long tableSize;

    private String exceptionList;

    private Long tenantId;

    private Long projectId;

    @Transient
    private String datasourceType;

    @Transient
    private String datasourceCode;

    /**
     * 封装后的objectName
     * 自定义SQL时需要包装一层
     */
    @Transient
    private String packageObjectName;

    @Transient
    private String sqlType;

}
