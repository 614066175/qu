package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>评估方案分组表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlanGroupDO extends AuditDomain {

    private Long groupId;

    private Long parentGroupId;

    private String groupCode;

    private String groupName;

    private String groupDesc;

    private String groupType;

    private Integer enabledFlag;

    private Long tenantId;

}
