package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>参考数据值实体</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_reference_data_value")
public class ReferenceDataValue extends AuditDomain {

    public static final String FIELD_VALUE_ID = "valueId";
    public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_VALUE_MEANING = "valueMeaning";
    public static final String FIELD_VALUE_SEQ = "valueSeq";
    public static final String FIELD_VALUE_DESC = "valueDesc";
    public static final String FIELD_PARENT_VALUE_ID = "parentValueId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long valueId;

    private Long dataId;

    private String value;

    private String valueMeaning;

    private Long valueSeq;

    private String valueDesc;

    private Long parentValueId;

    private Integer enabledFlag;

    private Long projectId;

    private Long tenantId;


}