package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>参考数据工作流记录表数据对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReferenceDataRecordDO extends AuditDomain {

    private Long recordId;

    private Long dataId;

    private Integer recordType;

    private Integer recordStatus;

    private Long instanceId;

    private Long projectId;

    private Long tenantId;

}
