package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import lombok.*;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/24 14:12
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
@Table(name = "xsta_standard_outbib")
public class StandardOutBib {

    @Id
    @GeneratedValue
    private Long outbibId;
    private Long standardId;
    private String standardType;
    private String fieldName;
    private String fieldDesc;
    private String datasourceCode;
    private String datasourceType;
    private String datasourceCatalog;
    private String datasourceSchema;
    private String tableName;
    private String tableDesc;
    private Long planId;
    private Long tenantId;

}
