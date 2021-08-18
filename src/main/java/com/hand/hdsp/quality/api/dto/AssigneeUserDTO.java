package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.hzero.export.annotation.ExcelSheet;

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
    private String id;
    private String loginName;
    private String realName;
}
