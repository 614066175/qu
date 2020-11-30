package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准落标表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardAimDO extends AuditDomain {

    private Long aimId;

    private Long standardId;

    private String standardType;

    private String fieldName;

    private String fieldDesc;

    private String datasourceCode;

    private String datasourceType;

    private String schemaName;

    private String tableName;

    private String tableDesc;

    private Long tenantId;

}
