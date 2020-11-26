package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>命名标准落标头表数据对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameStandardContentHeadDO extends AuditDomain {

    private Long contentHeadId;

    private Long nameStandardId;

    private String datasourceCode;

    private String datasourceType;

    private String excludeRole;

    private String excludeDesc;

    private Integer enabledFlag;

    private Long tenantId;

}
