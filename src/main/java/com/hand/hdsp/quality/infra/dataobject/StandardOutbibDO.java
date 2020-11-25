package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准落标表数据对象</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardOutbibDO extends AuditDomain {

    private Long outbibId;

    private Long standardId;

    private String standardType;

    private String fieldName;

    private String fieldDesc;

    private String datasourceCode;

    private String datasourceType;

    private String datasourceCatalog;

    private String datasourceSchema;

    private String tableName;

    private String tableDesc;

    private Long planId;

    private Long tenantId;

}
