package com.hand.hdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/02 12:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class StringTimeVO {

    private String start;

    private String end;
}
