package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p><p>
 *
 * @author lizheng  2022-04-20 8:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CodeVersion {

    @ApiModelProperty("代码集版本")
    private Long lovVersion;

    @ApiModelProperty("更新人")
    private Long lastUpdatedBy;

    @ApiModelProperty("更新时间")
    private Long lastUpdateDate;

    @ApiModelProperty("头表版本主键")
    private Long versionId;

    @ApiModelProperty("头表主键")
    private Long lovId;




}
