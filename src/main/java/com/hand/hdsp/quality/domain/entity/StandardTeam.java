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
 * <p>标准表实体</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_team")
public class StandardTeam extends AuditDomain {

    public static final String FIELD_STANDARD_TEAM_ID = "standardTeamId";
    public static final String FIELD_STANDARD_TEAM_CODE = "standardTeamCode";
    public static final String FIELD_STANDARD_TEAM_NAME = "standardTeamName";
    public static final String FIELD_STANDARD_TEAM_DESC = "standardTeamDesc";
    public static final String FIELD_PARENT_TEAM_ID = "parentTeamId";
    public static final String FIELD_INHERITE_TEAM_ID = "inheriteTeamId";
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
    private Long  standardTeamId;

    private String standardTeamCode;

    private String standardTeamName;

    private String standardTeamDesc;

    private Long parentTeamId;

    private Long inheriteTeamId;

    private Long tenantId;

    private Long projectId;


}