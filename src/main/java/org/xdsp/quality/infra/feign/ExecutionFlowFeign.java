package org.xdsp.quality.infra.feign;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.xdsp.quality.infra.feign.fallback.ExecutionFlowFallback;

/**
 * 执行流
 *
 * @author rui.jia01@hand-china.com 2020/03/27 15:39
 */
@FeignClient(
        value = "${xdsp.service.dispatch:xdsp-dispatch}",
        fallback = ExecutionFlowFallback.class
)
public interface ExecutionFlowFeign {

    /**
     *  获取flow日志
     *
     * @param tenantId
     * @param execId
     * @param jobId
     * @return
     */
    @GetMapping("/v1/{organizationId}/execution-flows/getExecFlowLogs")
    ResponseEntity<String> getJobLog(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId, @RequestParam("execId") int execId, @RequestParam("jobId") String jobId);
}
