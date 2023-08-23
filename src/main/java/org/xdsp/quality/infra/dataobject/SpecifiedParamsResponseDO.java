package org.xdsp.quality.infra.dataobject;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * <p>
 * 内置参数：通过http接口查询返回映射类
 * </p>
 *
 * @author abigballofmud 2020/02/04 14:00
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SpecifiedParamsResponseDO {

    @JsonAlias("_p_current_date_time")
    private String currentDataTime;
    @JsonAlias("_p_last_date_time")
    private String lastDateTime;
    @JsonAlias("_p_current_max_id")
    private String currentMaxId;
    @JsonAlias("_p_last_max_id")
    private String lastMaxId;

}
