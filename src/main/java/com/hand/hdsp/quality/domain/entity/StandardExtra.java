package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准附加信息表实体</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_extra")
public class StandardExtra extends AuditDomain {

    public static final String FIELD_EXTRA_ID = "extraId";
    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_STANDARD_TYPE = "standardType";
    public static final String FIELD_EXTRA_KEY = "extraKey";
    public static final String FIELD_EXTRA_VALUE = "extraValue";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PROJECT_ID = "projectId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long extraId;

    private Long standardId;

    private String standardType;

    private String extraKey;

    private String extraValue;

    private Long tenantId;

    private Long projectId;

}