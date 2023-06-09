package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>规则分组表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RuleGroupDO extends AuditDomain {

    private Long groupId;

    private Long parentGroupId;

    private String groupCode;

    private String groupName;

    private String groupDesc;

    private Long tenantId;

    private Long projectId;
}
