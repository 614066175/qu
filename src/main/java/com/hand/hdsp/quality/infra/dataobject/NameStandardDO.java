package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>命名标准表数据对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
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

    private String standardRole;

    private Integer ignoreCaseFlag;

    private Long chargeId;

    private Long chargeDeptId;

    private String chargeTel;

    private String chargeEmail;

    private Integer enabledFlag;

    private Long tenantId;

}
