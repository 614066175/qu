package org.xdsp.quality.infra.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 值域范围翻译结果VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValueRangeVo {
    private String code;
    private String name;
}