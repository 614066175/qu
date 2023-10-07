package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>实体</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_root_dic")
public class RootDic extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_DIC_NAME = "dicName";
    public static final String FIELD_DIC_URL = "dicUrl";
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
    private Long id;

    private String dicName;

    private String dicUrl;

    private Long tenantId;

    private Long projectId;


}