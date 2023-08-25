package org.xdsp.quality.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.infra.feign.fallback.AssetFeignFallBack;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/26 14:04
 * @since 1.0
 */
@FeignClient(
        value = "${xdsp.service.asset:xdsp-asset}",
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
