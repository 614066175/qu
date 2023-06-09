package org.xdsp.quality.infra.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import lombok.*;
import org.xdsp.quality.api.dto.WarningLevelDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;

/**
 * <p>
 * 消息对象
 * </p>
 *
 * @author lgl 2022/8/31 14:29
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDTO {

    private MeasureParamDO param;

    private BatchResultBase batchResultBase;

    private String sql;

    private WarningLevelDTO warningLevelDTO;
}
