package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.quality.api.dto.JobDTO;
import com.hand.hdsp.quality.infra.feign.fallback.DispatchJobFeignFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    ResponseEntity<String> createOrUpdate(@PathVariable(name = "organizationId") Long tenantId, @RequestBody JobDTO jobDTO);

}
