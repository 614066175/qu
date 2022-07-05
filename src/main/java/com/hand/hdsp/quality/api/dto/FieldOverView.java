package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>字段落标明细<p>
 *
 * @author lizheng  2022-05-19 9:17
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段落标统计")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldOverView {

    //  ====  非数据库字段
    private List<StandardStatisticsDTO> standardStatisticsDTOS;
    private Long totalRow ;
    private Long totalNullRow ;
    private Long totalCompliantRow ;
    private String totalCompliantRate;
    protected String totalAcompliantRate;


}
