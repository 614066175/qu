package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准分组表数据对象</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardGroupDO extends AuditDomain {

    private Long groupId;

    private Long parentGroupId;

    private String groupCode;

    private String groupName;

    private String groupDesc;

    private String groupType;

    private Integer enabledFlag;

    private Long tenantId;

}
