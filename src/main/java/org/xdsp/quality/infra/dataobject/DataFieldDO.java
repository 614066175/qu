package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>字段标准表数据对象</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DataFieldDO extends AuditDomain {

    private Long fieldId;

    private Long standardId;

    private Long groupId;

    private String fieldName;

    private String fieldComment;

    private String sysCommonName;

    private String standardDesc;

    private String fieldType;

    private String fieldLength;

    private String dataPattern;

    private String valueType;

    private String valueRange;

    private Long chargeDeptId;

    private Long chargeId;

    private String chargeTel;

    private String chargeEmail;

    private String standardStatus;

    private Integer fieldAccuracy;

    private Long tenantId;

    private Long projectId;


    private Integer notNullFlag;

    private String defaultValue;
    private Long dataStandardId;

    private String businessPurpose;

    private String businessRules;

    private String dataExample;

    private String standardAccord;

    private String accordContent;
}
