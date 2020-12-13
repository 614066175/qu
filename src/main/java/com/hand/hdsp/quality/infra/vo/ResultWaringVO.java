package com.hand.hdsp.quality.infra.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/02 18:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResultWaringVO {

    private String warningLevel;

    private Long countSum;

    private String exceptionInfo;

    private List<WarningLevelVO> warningLevelVOList;
}
