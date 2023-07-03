package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.util.Date;

/**
 * <p>loc独立值集表数据对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LocValueDO extends AuditDomain {

    private Long locValueId;

    private Long locId;

    private String locCode;

    private String value;

    private String meaning;

    private String description;

    private Long tenantId;

    private String tag;

    private Long orderSeq;

    private String parentValue;

    private Date startDateActive;

    private Date endDateActive;

    private Integer enabledFlag;

}
