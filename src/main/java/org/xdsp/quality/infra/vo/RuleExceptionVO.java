package org.xdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/01 17:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RuleExceptionVO {

    private Date date;

    private String ruleType;

    private Long exceptionRuleCount;
}
