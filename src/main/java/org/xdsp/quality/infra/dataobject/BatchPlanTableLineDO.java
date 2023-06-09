package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-表级规则校验项表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanTableLineDO extends AuditDomain {

    private Long planLineId;

    private Long planRuleId;

    private String checkItem;

    private String countType;

    private String customSql;

    private Long tenantId;

    private Long projectId;
}
