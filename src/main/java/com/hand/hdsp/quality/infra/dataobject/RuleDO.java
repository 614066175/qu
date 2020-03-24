package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>规则表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RuleDO extends AuditDomain {

    private Long ruleId;

    private String ruleModel;

    private Long groupId;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private String checkType;

    private String ruleType;

    private Integer exceptionBlock;

    private Long weight;

    private Integer enabledFlag;

    private Long tenantId;

}
