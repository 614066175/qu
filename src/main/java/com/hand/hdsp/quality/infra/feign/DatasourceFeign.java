package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.infra.feign.fallback.DatasourceFeignFallback;
import io.choerodon.core.domain.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 数据源
 *
 * @author feng.liu01@hand-china.com 2020-02-03 14:40:55
 */
@FeignClient(
        value = "${hdsp.service.core:hdsp-core}",
        fallback = DatasourceFeignFallback.class
)
public interface DatasourceFeign {

    /**
     * 查询数据源
     *
     * @param tenantId
     * @param datasourceName
     * @return
     */
    @GetMapping("/v1/{organizationId}/datasources")
    Page<DatasourceDTO> list(@PathVariable(name = "organizationId") Long tenantId,
                             @RequestParam(name = "datasourceName") String datasourceName);

    /**
     * 执行sql
     * 传递datasource字段信息以及额外的schema、sql
     *
     * @param tenantId
     * @param datasourceDTO
     * @return
     */
    @PostMapping("/v2/{organizationId}/datasources/exec-sql")
    ResponseEntity<String> execSql(@PathVariable(name = "organizationId") Long tenantId,
                                   @RequestBody DatasourceDTO datasourceDTO);

    /**
     * 执行分页查询sql
     * 传递datasource字段信息以及额外的schema、sql
     *
     * @param tenantId
     * @param datasourceDTO
     * @return
     */
    @PostMapping("/v2/{organizationId}/datasources/exec-query")
    ResponseEntity<String> execQuery(@PathVariable(name = "organizationId") Long tenantId,
                                     @RequestBody DatasourceDTO datasourceDTO);

    /**
     * 执行批量sql
     * 传递datasource字段信息以及额外的schema、sql
     *
     * @param tenantId
     * @param datasourceDTO
     * @return
     */
    @PostMapping("/v2/{organizationId}/datasources/exec-sql-batch")
    ResponseEntity<String> execSqlBatch(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestBody DatasourceDTO datasourceDTO);
}
