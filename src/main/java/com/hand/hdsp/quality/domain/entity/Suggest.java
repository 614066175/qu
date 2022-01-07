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
 * <p>问题知识库表实体</p>
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
@Table(name = "xqua_suggest")
public class Suggest extends AuditDomain {

    public static final String FIELD_SUGGEST_ID = "suggestId";
    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_PROBLEM_ID = "problemId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_SUGGEST_ORDER = "suggestOrder";
    public static final String FIELD_SUGGEST_CONTENT = "suggestContent";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long suggestId;

    private Long ruleId;

    private Long problemId;

    private Long tenantId;

    private Long suggestOrder;

    private String suggestContent;

    private Long projectId;

}