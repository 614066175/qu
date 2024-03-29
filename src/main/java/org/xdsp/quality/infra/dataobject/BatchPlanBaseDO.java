package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-基础配置表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanBaseDO extends AuditDomain {

    private Long planBaseId;

    private Long planId;

    private String datasourceType;

    private Long datasourceId;

    private String datasourceSchema;

    private String sqlType;

    private String objectName;

    private String description;

    private String incrementStrategy;

    private String incrementColumn;

    private String whereCondition;

    private Long tenantId;

    private Long projectId;
}
