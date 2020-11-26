package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>命名标准落标排除表数据对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameStandardContentExcludeDO extends AuditDomain {

    private Long contentExcludeId;

    private Long contentLineId;

    private String tableCode;

    private Long tenantId;

}
