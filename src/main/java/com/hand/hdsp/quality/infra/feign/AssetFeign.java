package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.infra.feign.fallback.AssetFeignFallBack;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/26 14:04
 * @since 1.0
 */
@FeignClient(
        value = "${hdsp.service.asset:hdsp-asset}",
        fallback = AssetFeignFallBack.class
)
public interface AssetFeign {


    /**
     * 数据标准存es
     * @param tenantId
     * @param dataStandardDTO
     * @return
     */
    @PostMapping("/v1/{organizationId}/asset-fields/save-standard-to-es")
    ResponseEntity<?> saveStandardToEs(@PathVariable("organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO);


    /**
     * 将数据标准从es删除
     * @param tenantId
     * @param dataStandardDTO
     * @return
     */
    @PostMapping("/v1/{organizationId}/asset-fields/delete-Standard-to-es")
    ResponseEntity<?> deleteStandardToEs(@PathVariable("organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO);
}
