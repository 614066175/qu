package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>数据标准版本表实体</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_data_standard_version")
public class DataStandardVersion extends AuditDomain {

    public static final String FIELD_VERSION_ID = "versionId";
    public static final String FIELD_GROUP_ID="groupId";
    public static final String FIELD_STANDARD_ID = "standardId";
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
    public static final String FIELD_ACCORD_CONTENT = "accordContent";
    public static final String FIELD_CHARGE_DEPT_ID = "chargeDeptId";
    public static final String FIELD_CHARGE_ID = "chargeId";
    public static final String FIELD_CHARGE_TEL = "chargeTel";
    public static final String FIELD_CHARGE_EMAIL = "chargeEmail";
    public static final String FIELD_VERSION_NUMBER = "versionNumber";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_RELEASE_BY = "releaseBy";
    public static final String FIELD_RELEASE_DATE = "releaseDate";
    public static final String FIELD_NULL_FLAG = "nullFlag";
    public static final String FIELD_BUSINESS_RULES = "businessRules";
    public static final String FIELD_DATA_EXAMPLE = "dataExample";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long versionId;

    private Long groupId;

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

    private Long releaseBy;

    private Date releaseDate;

    private Integer nullFlag;

    private String businessPurpose;

    private String businessRules;

    private String dataExample;

}