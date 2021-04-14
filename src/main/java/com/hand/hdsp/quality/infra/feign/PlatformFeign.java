package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.quality.infra.feign.fallback.PlatformFeignFallBack;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/04/12 20:44
 * @since 1.0
 */
@FeignClient(
        value = "${hzero.service.platform.name:hzero-platform}",
        fallback = PlatformFeignFallBack.class
)
public interface PlatformFeign {


    /**
     * 查询所有值集数据
     * @param lovCode
     * @param organizationId
     * @param tag
     * @param page
     * @param size
     * @param tenantId
     * @return
     */
    @GetMapping({"/v1/{organizationId}/lovs/data"})
    ResponseEntity<String> queryLovData(@ApiParam(value = "值集代码",required = true) @RequestParam String lovCode,
                                        @ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
                                        @ApiParam("值tag") @RequestParam(required = false) String tag, @ApiParam("page") @RequestParam(required = false) Integer page,
                                        @ApiParam("size") @RequestParam(required = false) Integer size, @ApiParam("租户ID") @RequestParam(required = false) Long tenantId);
}
