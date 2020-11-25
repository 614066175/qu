package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * 标准分组表
 * </p>
 *
 * @author lgl 2020/11/23 20:53
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_group")
public class StandardGroup extends AuditDomain {

    public static final StandardGroup ROOT_STANDARD_GROUP = StandardGroup.builder()
            .groupId(0L)
            .groupCode("root")
            .groupName("分组")
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
