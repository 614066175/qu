package org.xdsp.quality.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.quality.api.dto.TimestampControlDTO;
import org.xdsp.quality.infra.feign.fallback.TimestampFallback;

/**
 * 时间戳feign
 *
 * @author terry
 * @version 1.0
 * @date 2020/2/4 10:11
 */
@FeignClient(
        value = "${hdsp.service.dispatch:hdsp-dispatch}",
        fallback = TimestampFallback.class
)
public interface TimestampFeign {

    /**
     * 创建时间戳
     *
     * @param tenantId            租户ID
     * @param timestampControlDTO 时间戳对象
     * @return 时间戳对象
     */
    @PostMapping("/v2/{organizationId}/timestamp-controls/create-update")
    ResponseEntity<String> createOrUpdateTimestamp(@PathVariable(name = "organizationId") Long tenantId,
                                                   @RequestBody TimestampControlDTO timestampControlDTO);

    /**
     * 获取增量控制参数
     *
     * @param tenantId
     * @param timestampType
     * @return
     */
    @GetMapping("/v2/{organizationId}/timestamp-controls/get-increment-param")
    ResponseEntity<String> getIncrementParam(@PathVariable(name = "organizationId") Long tenantId,
                                             @RequestParam("timestampType") String timestampType,
                                             @RequestParam("projectId") Long projectId);

    /**
     * 更新增量参数
     *
     * @param tenantId
     * @param timestampControlDTO
     * @return
     */
    @PostMapping("/v2/{organizationId}/timestamp-controls/update-increment")
    ResponseEntity<String> updateIncrement(@PathVariable(name = "organizationId") Long tenantId,
                                           @RequestBody TimestampControlDTO timestampControlDTO);

}
