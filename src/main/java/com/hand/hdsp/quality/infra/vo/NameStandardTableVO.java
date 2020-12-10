package com.hand.hdsp.quality.infra.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author 张鹏 2020/12/10 11:06
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NameStandardTableVO {

    private String schema;
    private List<String> tables;
}
