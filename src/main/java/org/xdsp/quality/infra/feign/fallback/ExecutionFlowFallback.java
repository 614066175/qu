package org.xdsp.quality.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.feign.ExecutionFlowFeign;

/**
 * 执行流 Fallback
 *
 * @author rui.jia01@hand-china.com 2020/03/27 15:40
 */
@Component
public class ExecutionFlowFallback implements ExecutionFlowFeign {

    @Override
    public ResponseEntity<String> getJobLog(Long tenantId, int execId, String jobId) {
        throw new CommonException(ErrorCode.DISPATCH_GET_JOB_LOG);
    }
}
