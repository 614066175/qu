package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>问题知识库表数据对象</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SuggestDO extends AuditDomain {

    private Long suggestId;

    private Long ruleId;

    private Long problemId;

    private Long tenantId;

    private Long suggestOrder;

    private String suggestContent;

    private Long projectId;

}
