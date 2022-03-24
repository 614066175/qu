package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>命名标准表实体</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_name_standard")
public class NameStandard extends AuditDomain {

    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_STANDARD_CODE = "standardCode";
    public static final String FIELD_STANDARD_NAME = "standardName";
    public static final String FIELD_STANDARD_DESC = "standardDesc";
    public static final String FIELD_STANDARD_TYPE = "standardType";
    public static final String FIELD_STANDARD_RULE = "standardRule";
    public static final String FIELD_IGNORE_CASE_FLAG = "ignoreCaseFlag";
    public static final String FIELD_CHARGE_ID = "chargeId";
    public static final String FIELD_CHARGE_DEPT_ID = "chargeDeptId";
    public static final String FIELD_CHARGE_TEL = "chargeTel";
    public static final String FIELD_CHARGE_EMAIL = "chargeEmail";
    public static final String FIELD_LATEST_CHECKED_STATUS = "latestCheckedStatus";
    public static final String FIELD_LATEST_ABNORMAL_NUM = "latestAbnormalNum";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PROJECT_ID = "projectId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long standardId;

    private Long groupId;

    private String standardCode;

    private String standardName;

    private String standardDesc;

    private String standardType;

    private String standardRule;

    private Integer ignoreCaseFlag;

    private Long chargeId;

    private Long chargeDeptId;

    private String chargeTel;

    private String chargeEmail;

    private Integer enabledFlag;

    private String latestCheckedStatus;

    private Long latestAbnormalNum;

    private Long tenantId;

    private Long projectId;

}