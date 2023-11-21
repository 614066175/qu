package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>字段标准表实体</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xsta_field_standard")
public class DataField extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long fieldId;

    private Long groupId;

    private Long dataStandardId;

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

    private Integer nullFlag;

    private String defaultValue;

    private Long releaseBy;

    private Date releaseDate;

    private String businessPurpose;

    private String businessRules;

    private String dataExample;

    private String standardAccord;

    private String accordContent;
}