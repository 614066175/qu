package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>数据标准表实体</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:20:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_data_standard")
public class DataStandard extends AuditDomain {

    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_VERSION_NUMBER = "versionNumber";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_STANDARD_CODE = "standardCode";
    public static final String FIELD_STANDARD_NAME = "standardName";
    public static final String FIELD_STANDARD_DESC = "standardDesc";
    public static final String FIELD_DATA_TYPE = "dataType";
    public static final String FIELD_DATA_PATTERN = "dataPattern";
    public static final String FIELD_LENGTH_TYPE = "lengthType";
    public static final String FIELD_DATA_LENGTH = "dataLength";
    public static final String FIELD_VALUE_TYPE = "valueType";
    public static final String FIELD_VALUE_RANGE = "valueRange";
    public static final String FIELD_STANDARD_ACCORD = "standardAccord";
    public static final String FIELD_STANDARD_SOURCE = "standardSource";
    public static final String FIELD_CHARGE_DEPT_ID = "chargeDeptId";
    public static final String FIELD_CHARGE_ID = "chargeId";
    public static final String FIELD_CHARGE_TEL = "chargeTel";
    public static final String FIELD_CHARGE_EMAIL = "chargeEmail";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

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