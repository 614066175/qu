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
 * <p>评估方案分组表实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_plan_group")
public class PlanGroup extends AuditDomain {

    public static final PlanGroup ROOT_PLAN_GROUP = PlanGroup.builder()
            .groupId(0L)
            .groupCode("root")
            .groupName("所有分组")
            .build();

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

    private String groupType;

    private Integer enabledFlag;

    private Long tenantId;


}
