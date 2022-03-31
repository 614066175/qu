package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准附加信息表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardExtraDO extends AuditDomain {

    private Long extraId;

    private Long standardId;

    private String standardType;

    private String extraKey;

    private String extraValue;

    private Long tenantId;

    private Long projectId;
}
