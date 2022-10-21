package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * <p>
 * 异常数据DTO
 * </p>
 *
 * @author lgl 2021/01/05 11:16
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("异常数据")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDataDTO extends AuditDomain {
   private Long planBaseId;

   private String ruleName;

   private String warningLevel;

   private Long tenantId;

   private Long projectId;
}
