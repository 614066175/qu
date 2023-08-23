package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>命名标准表数据对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameStandardDO extends AuditDomain {

    private Long standardId;

    private Long groupId;

    private String standardCode;

    private String standardName;

    private String standardDesc;

    private String standardType;

    private String standardRule;

    private Integer ignoreCaseFlag;

    private Long chargeId;

    private Long chargeDeptId;

    private String chargeTel;

    private String chargeEmail;

    private String latestCheckedStatus;

    private Long latestAbnormalNum;

    private Integer enabledFlag;

    private Long tenantId;

    private Long projectId;

}
