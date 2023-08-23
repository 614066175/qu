package org.xdsp.quality.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.TimestampControlDTO;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.feign.TimestampFeign;

/**
 * 时间戳feign
 *
 * @author terry
 * @version 1.0
 * @date 2020/2/4 10:11
 */
@Slf4j
@Component
public class TimestampFallback implements TimestampFeign {

    @Override
    public ResponseEntity<String> createOrUpdateTimestamp(Long tenantId, TimestampControlDTO timestampControlDTO) {
        log.error("create timestamp error");
        throw new CommonException(ErrorCode.CREATE_OR_UPDAT_TIMESTAMP);
    }

    @Override
    public ResponseEntity<String> getIncrementParam(Long tenantId, String timestampType,Long projectId) {
        throw new CommonException(ErrorCode.GET_INCREMENT_PARAM);
    }

    @Override
    public ResponseEntity<String> updateIncrement(Long tenantId, TimestampControlDTO timestampControlDTO) {
        throw new CommonException(ErrorCode.UPDATE_INCREMENT);
    }
}
