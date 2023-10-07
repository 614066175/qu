package org.xdsp.quality.infra.feign;

import io.swagger.annotations.ApiParam;
import org.hzero.boot.workflow.dto.RunInstance;
import org.hzero.boot.workflow.feign.fallback.HwkfRemoteServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/02/26 16:02
 * @since 1.0
 */
@FeignClient(
        value = "${hzero.service.workflow.name:hzero-workflow}",
        path = "/v1",
        fallback = HwkfRemoteServiceImpl.class
)
public interface WorkFlowFeign {


    @PostMapping({"/{organizationId}/personal-process/start/{flowKey}"})
     ResponseEntity<RunInstance> startInstanceByFlowKey(@ApiParam(value = "租户ID",required = true) @PathVariable("organizationId") Long tenantId, @ApiParam(value = "流程定义编码",required = true) @PathVariable String flowKey, @ApiParam(value = "业务主键",required = true) @RequestParam String businessKey, @ApiParam(value = "流程启动维度",required = true) @RequestParam String dimension, @ApiParam(value = "流程启动人",required = true) @RequestParam String starter, @ApiParam("启动参数") @RequestParam(required = false) Map<String, Object> variableMap);
}
