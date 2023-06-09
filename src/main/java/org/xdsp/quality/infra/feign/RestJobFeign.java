package org.xdsp.quality.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.xdsp.quality.api.dto.RestJobDTO;
import org.xdsp.quality.infra.feign.fallback.RestJobFeignFallBack;

/**
 * rest任务
 *
 * @author feng.liu01@hand-china.com 2020-02-03 14:40:55
 */
@FeignClient(
        value = "${hdsp.service.factory:hdsp-factory}",
        fallback = RestJobFeignFallBack.class
)
public interface RestJobFeign {

    /**
     * 创建或更新rest任务
     *
     * @param tenantId
     * @param restJobDTO
     * @return
     */
    @PostMapping("/v1/{organizationId}/rest-jobs")
    ResponseEntity<String> create(@PathVariable("organizationId") Long tenantId,
                                  @RequestBody RestJobDTO restJobDTO);

    /**
     * 查询
     *
     * @param tenantId
     * @param jobName
     * @return
     */
    @GetMapping("/v1/{organizationId}/rest-jobs/{jobName}")
    ResponseEntity<String> findName(@PathVariable(name = "organizationId") Long tenantId,
                                    @PathVariable String jobName);
}
