package org.xdsp.quality.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.LineageDTO;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.feign.LineageFeign;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/10/18 16:26
 * @since 1.0
 */
@Component
@Slf4j
public class LineageFeignFallBack implements LineageFeign {
    @Override
    public ResponseEntity<List<LineageDTO>> updateLineageStatus(Long tenantId, List<LineageDTO> lineageDTOS) {
        throw new CommonException(ErrorCode.LINEAGE_ERROR);
    }
}
