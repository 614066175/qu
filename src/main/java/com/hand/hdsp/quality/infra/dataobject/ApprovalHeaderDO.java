package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>申请头表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApprovalHeaderDO extends AuditDomain {

    private Long approvalId;

    private String resourceName;

    private String resourceDesc;

    private String itemType;

    private String operation;

    private Long applyId;

    private Long tenantId;

}
