package org.xdsp.quality.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.feign.PlatformFeign;

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
