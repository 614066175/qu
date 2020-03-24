package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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

}
