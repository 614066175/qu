package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.JobDTO;
import com.hand.hdsp.quality.infra.feign.fallback.DispatchJobFeignFallBack;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author lei.xie03@hand-china.com 2019/05/31 14:52
 */
@FeignClient(
        value = "${hdsp.service.dispatch:hdsp-dispatch}",
        fallback = DispatchJobFeignFallBack.class
)
public interface DispatchJobFeign {

    /**
     * 根据任务名创建或者更新
     *
     * @param tenantId 租户id
     * @param jobDTO   job
     * @return job
     */
    @PostMapping("/v1/{organizationId}/dispatch-jobs/create-update")
    ResponseEntity<String> createOrUpdate(@PathVariable(name = "organizationId") Long tenantId, @RequestParam Long projectId, @RequestBody JobDTO jobDTO);


    @GetMapping("/v1/{organizationId}/dispatch-jobs/find-by-name")
    ResponseEntity<String> findByName(@ApiParam(value = "租户id", required = true)
                                        @PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        @RequestParam String jobName);
}
