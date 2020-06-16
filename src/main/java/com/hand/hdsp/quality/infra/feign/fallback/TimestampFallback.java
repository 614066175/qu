package com.hand.hdsp.quality.infra.feign.fallback;

import com.hand.hdsp.quality.api.dto.TimestampControlDTO;
import com.hand.hdsp.quality.infra.feign.TimestampFeign;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
        throw new CommonException("error.feign.timestamp.create_or_update_timestamp");
    }

    @Override
    public ResponseEntity<String> getIncrementParam(Long tenantId, String timestampType) {
        throw new CommonException("error.feign.timestamp.get_increment_param");
    }

    @Override
    public ResponseEntity<String> updateIncrement(Long tenantId, TimestampControlDTO timestampControlDTO) {
        throw new CommonException("error.feign.timestamp.update_increment");
    }
}
