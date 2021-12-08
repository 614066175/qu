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
 * <p>问题库表实体</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_problem")
public class Problem extends AuditDomain {

    public static final String FIELD_PROBLEM_ID = "problemId";
    public static final String FIELD_PROBLEM_PARENT_ID = "problemParentId";
    public static final String FIELD_PROBLEM_CODE = "problemCode";
    public static final String FIELD_PROBLEM_NAME = "problemName";
    public static final String FIELD_PROBLEM_TYPE = "problemType";
    public static final String FIELD_PROBLEM_DESC = "problemDesc";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long problemId;

    private Long problemParentId;

    private String problemCode;

    private String problemName;

    private String problemType;

    private String problemDesc;

    private Long tenantId;

    private Long projectId;

}