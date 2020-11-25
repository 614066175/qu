package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准额外信息表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 17:27:05
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

    private Long versionNumber;

    private String extraKey;

    private String extraValue;

    private Long tenantId;

}
