package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/8/17 16:03
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssigneeUserDTO {
    /**
     * 员工名称
     */
    private String employeeName;
    /**
     * 员工编码
     */
    private String employeeNum;
}
