package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RootDicDO extends AuditDomain {

    private Long id;

    private String dicName;

    private String dicUrl;

    private Long tenantId;

    private Long projectId;

}
