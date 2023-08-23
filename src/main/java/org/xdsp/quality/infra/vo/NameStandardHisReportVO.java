package org.xdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author 张鹏 2020/12/14 10:20
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NameStandardHisReportVO {

    /**
     * 横轴
     */
    private String date;

    /**
     * 平均异常数量
     */
    private Long averageAbnormalNum;
}
