package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>标准表 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardTeamDTO extends AuditDomain {

    @ApiModelProperty("标准组主键")
    private Long  standardTeamId;

    @ApiModelProperty(value = "标准组编码")
    @NotBlank
    @Size(max = 120)
    private String standardTeamCode;

    @ApiModelProperty(value = "标准组名称")
    @NotBlank
    @Size(max = 120)
    private String standardTeamName;

    @ApiModelProperty(value = "标准组描述")
    private String standardTeamDesc;

    @ApiModelProperty(value = "父级字段标准组id")
    private Long parentTeamId;

    @ApiModelProperty(value = "继承自标准组id")
    private Long inheriteTeamId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    @NotNull
    private Long projectId;

    @Transient
    private String creatorName;

    @Transient
    private String updaterName;

    @Transient
    private String parentTeamCode;

    @Transient
    private String parentTeamName;

    @Transient
    private String inheriteTeamCode;

    @Transient
    private String inheriteTeamName;

    //当前标准组id，继承自时排除当前标准组
    @Transient
    private Long curStandardTeamId;

    @Transient
    private List<DataFieldDTO> dataFieldDTOList;

}
