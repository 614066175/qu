package com.hand.hdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * 告警结果
 *
 * @author rui.jia01@hand-china.com 2020/04/01 17:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class WarningLevelVO {

    private String warningLevel;

    @Builder.Default
    private Long levelCount=1L;
}
