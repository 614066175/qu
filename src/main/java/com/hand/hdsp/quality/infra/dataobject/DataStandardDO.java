package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>数据标准表数据对象</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:20:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DataStandardDO extends AuditDomain {

    private Long standardId;

    private Long versionNumber;

    private Long groupId;

    private String standardCode;

    private String standardName;

    private String standardDesc;

    private String dataType;

    private String dataPattern;

    private String lengthType;

    private String dataLength;

    private String valueType;

    private String valueRange;

    private String standardAccord;

    private String standardSource;

    private Long chargeDeptId;

    private Long chargeId;

    private String chargeTel;

    private String chargeEmail;

    private String status;

    private Long tenantId;

}
