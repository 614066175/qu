package com.hand.hdsp.quality.infra.feign.fallback;

import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.feign.PlatformFeign;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/04/12 20:46
 * @since 1.0
 */
@Slf4j
@Component
public class PlatformFeignFallBack implements PlatformFeign {

    @Override
    public ResponseEntity<String> queryLovData(String lovCode, Long organizationId, String tag, Integer page, Integer size, Long tenantId) {
        throw new CommonException(ErrorCode.NOT_FIND_VALUE);
    }
}
