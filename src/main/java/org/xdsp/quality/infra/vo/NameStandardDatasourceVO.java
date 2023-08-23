package org.xdsp.quality.infra.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author 张鹏 2020/12/10 14:06
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NameStandardDatasourceVO {
    private String datasource;
    private List<String> schemas;
}
