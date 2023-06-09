package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>命名标准执行历史详情表数据对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameExecHisDetailDO extends AuditDomain {

    private Long detailId;

    private Long historyId;

    private String schemaName;

    private String tableName;

    private String errorMessage;

    private String sourcePath;

    private Long tenantId;

    private Long projectId;

}
