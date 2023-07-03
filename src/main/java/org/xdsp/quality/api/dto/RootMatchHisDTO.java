package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * <p>字段标准匹配记录表 数据传输对象</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段标准匹配记录表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootMatchHisDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "批次号")
    @NotBlank
    @Size(max = 120)
    private String batchNumber;

    @ApiModelProperty(value = "数据数量")
    private Long dataCount;

    @ApiModelProperty(value = "当前状态")
    @NotBlank
    @Size(max = 30)
    private String status;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @Transient
    private Date startDate;

    @Transient
    private Date endDate;

}
