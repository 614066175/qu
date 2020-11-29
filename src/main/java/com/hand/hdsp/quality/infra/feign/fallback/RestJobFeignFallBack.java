package com.hand.hdsp.quality.infra.feign.fallback;

import com.hand.hdsp.quality.api.dto.RestJobDTO;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.feign.RestJobFeign;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author lei.xie03@hand-china.com 2019/05/31 14:58
 */
@Slf4j
@Component
public class RestJobFeignFallBack implements RestJobFeign {


    @Override
    public ResponseEntity<String> create(Long tenantId, RestJobDTO restJobDTO) {
        throw new CommonException(ErrorCode.REST_JOB_CREATE);
    }

    @Override
    public ResponseEntity<String> findName(Long tenantId, String jobName) {
        throw new CommonException(ErrorCode.REST_JOB_FIND_NAME);
    }
}
