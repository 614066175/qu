package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>实时数据方案结果表实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_streaming_result")
public class StreamingResult extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long resultId;

    private Long planId;

    private BigDecimal mark;

    private Date startDate;

    private Date endDate;

    private String planStatus;

    private Date nextCountDate;

    private Long tenantId;

    private Long projectId;

}
