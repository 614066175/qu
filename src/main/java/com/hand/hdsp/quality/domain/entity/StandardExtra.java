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
 * @author lgl 2020/11/25 10:33
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
@Table(name = "xsta_standard_extra")
public class StandardExtra extends AuditDomain {

    @Id
    @GeneratedValue
    private Long extraId;
    private Long standardId;
    private String standardType;
    private Long versionNumber;
    private String extraKey;
    private String extraValue;
    private Long tenantId;
}
