package org.xdsp.quality.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.infra.feign.fallback.ModelFeignFallBack;

/**
 * @Author: dongwl
 * @Date: 2022/06/22
 */
@FeignClient(
        value = "${hdsp.service.model:hdsp-model}",
        fallback = ModelFeignFallBack.class
)
public interface ModelFeign {

    /**
     * 表设计表对应字段列表
     *
     * @param tenantId
     * @param projectId
     * @param customTableId
     * @return
     */
    @GetMapping("/v1/{organizationId}/table-columns")
    ResponseEntity<String> list(@PathVariable(name = "organizationId") Long tenantId,
                                @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                @RequestParam Long customTableId);


    /**
     * 查找表设计
     * @param tenantId
     * @param projectId
     * @param customDatasourceType
     * @param customDatasourceCode
     * @param customSchemaName
     * @param customTableName
     * @return
     */
    @GetMapping("/v1/{organizationId}/custom-tables/detail")
    ResponseEntity<String> detailForFeign(@PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          @RequestParam String customDatasourceType,
                                          @RequestParam String customDatasourceCode,
                                          @RequestParam String customSchemaName,
                                          @RequestParam String customTableName);
}
