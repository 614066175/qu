package org.xdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

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

    /**
     * 告警等级json
     */
    private String warningLevel;

    private Long countSum;

    private String exceptionInfo;

    /**
     * 告警等级json转换的对象
     */
    private List<WarningLevelVO> warningLevelVOList;
}
