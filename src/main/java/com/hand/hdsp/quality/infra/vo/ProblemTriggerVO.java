package com.hand.hdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * description
 *
 * @author 23168 2021/08/18 11:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ProblemTriggerVO {

    private Long problemId;

    private String problemName;

    private String ruleType;

    private String columnType;

    private String fieldName;

    private Long triggerCount;
}
