package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-表间规则关联关系表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanRelTableLineDO extends AuditDomain {

    private Long lineId;

    private Long planRelTableId;

    private String sourceFieldName;

    private String relCode;

    private String relFieldName;

    private Long tenantId;

}
