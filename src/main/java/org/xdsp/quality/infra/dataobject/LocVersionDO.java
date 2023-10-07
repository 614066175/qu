package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>loc表数据对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LocVersionDO extends AuditDomain {

    private Long versionId;

    private Long locId;

    private String locCode;



    private String locName;

    private String description;

    private Long tenantId;

    private String parentlocCode;

    private Long parentTenantId;



    private String valueField;

    private String displayField;

    private Integer mustPageFlag;

    private Integer enabledFlag;



    private Long locVersion;

}
