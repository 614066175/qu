package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:43
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准额外信息表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class StandardExtraDTO extends AuditDomain {

    @ApiModelProperty(value = "表主键ID")
    private Long extraId;
    @ApiModelProperty(value = "标准ID")
    private Long standardId;
    @ApiModelProperty(value = "标准类型")
    private String standardType;
    @ApiModelProperty(value = "标准版本号")
    private Long versionNumber;
    @ApiModelProperty(value = "键")
    private String extraKey;
    @ApiModelProperty(value = "值")
    private String extraValue;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
}
