package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准文档管理表数据对象</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardDocDO extends AuditDomain {

    private Long docId;

    private Long groupId;

    private String standardCode;

    private String standardName;

    private String standardDesc;

    private String docName;

    private String docPath;

    private Long tenantId;

}
