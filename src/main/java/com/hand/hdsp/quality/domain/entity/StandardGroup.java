package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准分组表实体</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_group")
public class StandardGroup extends AuditDomain {

    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_PARENT_GROUP_ID = "parentGroupId";
    public static final String FIELD_GROUP_CODE = "groupCode";
    public static final String FIELD_GROUP_NAME = "groupName";
    public static final String FIELD_GROUP_DESC = "groupDesc";
    public static final String FIELD_STANDARD_TYPE = "standardType";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
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
    private Long groupId;

    private Long parentGroupId;

    private String groupCode;

    private String groupName;

    private String groupDesc;

    private String standardType;

    private Integer enabledFlag;

    private Long tenantId;

    private Long projectId;

}