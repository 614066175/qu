package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>LOV表数据对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LovDO extends AuditDomain {

    private Long lovId;

    private String lovCode;

    private String lovTypeCode;

    private String routeName;

    private String lovName;

    private String description;

    private Long tenantId;

    private String parentLovCode;

    private Long parentTenantId;

    private String customSql;

    private String customUrl;

    private String valueField;

    private String displayField;

    private Integer mustPageFlag;

    private Integer enabledFlag;

    private String translationSql;

    private Integer publicFlag;

    private String encryptField;

    private String decryptField;

    private String requestMethod;

}
