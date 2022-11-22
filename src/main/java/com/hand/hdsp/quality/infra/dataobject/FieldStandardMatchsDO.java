package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>字段标准匹配记录表数据对象</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FieldStandardMatchsDO extends AuditDomain {

    private Long id;

    private String batchNumber;

    private Long dataCount;

    private String status;

    private Long projectId;

    private Long tenantId;

}
