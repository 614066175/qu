package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.util.Date;

/**
 * <p>loc独立值集表数据对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LocValueVersionDO extends AuditDomain {

    private Long valueVersionId;

    private Long locVersionId;

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
