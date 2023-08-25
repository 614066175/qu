package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>loc独立值集表实体</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_loc_value_version")
public class LocValueVersion extends AuditDomain {

    public static final String FIELD_VALUE_VERSION_ID = "valueVersionId";
    public static final String FIELD_LOC_VERSION_ID = "locVersionId";
    public static final String FIELD_LOC_VALUE_ID = "locValueId";
    public static final String FIELD_LOC_ID = "locId";
    public static final String FIELD_LOC_CODE = "locCode";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_MEANING = "meaning";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TAG = "tag";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_PARENT_VALUE = "parentValue";
    public static final String FIELD_START_DATE_ACTIVE = "startDateActive";
    public static final String FIELD_END_DATE_ACTIVE = "endDateActive";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long valueVersionId;

    private Long locVersionId;

    private Long locVersion;

    private Long locValueId;

    private Long locId;

    private String locCode;

    private String value;

    private String meaning;

    private String description;

    private Long tenantId;

    private String tag;

    private Long orderSeq;

    private String parentValue;

    private Date startDateActive;

    private Date endDateActive;

    private Integer enabledFlag;


}