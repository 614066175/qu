package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>字段标准匹配表数据对象</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FieldStandardMatchingDO extends AuditDomain {

    private Long id;

    private String fieldName;

    private String fieldType;

    private String fieldComment;

    private String fieldDescription;

    private String matchingStatus;

    private String source;

    private Long projectId;

    private Long tenantId;

}
