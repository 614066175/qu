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
 * <p>参考数据头表实体</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_reference_data_history")
public class ReferenceDataHistory extends AuditDomain {

    public static final String FIELD_HISTORY_ID = "historyId";
    public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_DATA_CODE = "dataCode";
    public static final String FIELD_DATA_NAME = "dataName";
    public static final String FIELD_DATA_DESC = "dataDesc";
    public static final String FIELD_PARENT_DATA_ID = "parentDataId";
    public static final String FIELD_DATA_GROUP_ID = "dataGroupId";
    public static final String FIELD_DATA_VALUE_JSON = "dataValueJson";
    public static final String FIELD_VERSION_NUMBER = "versionNumber";
    public static final String FIELD_RELEASE_BY = "releaseBy";
    public static final String FIELD_RELEASE_DATE = "releaseDate";
    public static final String FIELD_RESPONSIBLE_DEPT_ID = "responsibleDeptId";
    public static final String FIELD_RESPONSIBLE_PERSON_ID = "responsiblePersonId";
    public static final String FIELD_RESPONSIBLE_PERSON_TEL = "responsiblePersonTel";
    public static final String FIELD_RESPONSIBLE_PERSON_EMAIL = "responsiblePersonEmail";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long historyId;

    private Long dataId;

    private String dataCode;

    private String dataName;

    private String dataDesc;

    private Long parentDataId;

    private Long dataGroupId;

    private String dataValueJson;

    private Long versionNumber;

    private Long releaseBy;

    private Date releaseDate;

    private Long responsibleDeptId;

    private Long responsiblePersonId;

    private String responsiblePersonTel;

    private String responsiblePersonEmail;

    private Long projectId;

    private Long tenantId;


}