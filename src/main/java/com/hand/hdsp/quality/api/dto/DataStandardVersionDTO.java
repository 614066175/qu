package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:42
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("数据标准版本表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class DataStandardVersionDTO extends AuditDomain {

}
