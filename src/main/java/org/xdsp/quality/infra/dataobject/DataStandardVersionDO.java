package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>数据标准版本表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DataStandardVersionDO extends AuditDomain {

    private Long versionId;

    private Long standardId;

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

    private String accordContent;

    private Long chargeDeptId;

    private Long chargeId;

    private String chargeTel;

    private String chargeEmail;

    private Long versionNumber;

    private Long tenantId;

    private Long projectId;

    private String businessPurpose;

    private String businessRules;

    private String dataExample;


}
