package com.hand.hdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/05 13:02
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ColumnVO {
    private String columnName;
    private Boolean selectable;
    private String typeName;
    private String columnDesc;
}
