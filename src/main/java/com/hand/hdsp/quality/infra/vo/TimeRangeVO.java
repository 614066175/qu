package com.hand.hdsp.quality.infra.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/02 9:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TimeRangeVO {

    private Date startDate;

    private Date endDate;
}
