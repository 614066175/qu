package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/24 14:02
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xsta_data_standard")
public class DataStandard extends AuditDomain {

    @Id
    @GeneratedValue
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
