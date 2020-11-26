package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.quality.api.dto.ApprovalHeaderDTO;
import com.hand.hdsp.quality.api.dto.ApprovalLineDTO;
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
     * 创建申请头
     *
     * @param tenantId
     * @param approvalHeaderDTO
     * @return
     */
    @PostMapping("/v1/{organizationId}/approval-headers")
    ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody ApprovalHeaderDTO approvalHeaderDTO);


    /**
     * 删除申请头
     *
     * @param tenantId
     * @param approvalHeaderDTO
     * @return
     */
    @DeleteMapping("/v1/{organizationId}/approval-headers")
    ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                             @RequestBody ApprovalHeaderDTO approvalHeaderDTO);

    /**
     * 修改申请头
     *
     * @param tenantId
     * @param approvalHeaderDTO
     * @return
     */
    @PutMapping("/v1/{organizationId}/approval-headers")
    ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody ApprovalHeaderDTO approvalHeaderDTO);

    /**
     * 根据唯一索引查询申请头
     *
     * @param tenantId
     * @param approvalHeaderDTO
     * @return
     */
    @GetMapping("/v1/{organizationId}/approval-headers/get-by-unique")
    ResponseEntity<ApprovalHeaderDTO> getByUnique(@PathVariable(name = "organizationId") Long tenantId,
                                                  ApprovalHeaderDTO approvalHeaderDTO);


    /**
     * 创建申请行
     *
     * @param tenantId
     * @param approvalLineDTO
     * @return
     */
    @PostMapping("/v1/{organizationId}/approval-lines")
    ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody ApprovalLineDTO approvalLineDTO);


    /**
     * 修改申请行
     *
     * @param tenantId
     * @param approvalLineDTO
     * @return
     */
    @PutMapping("/v1/{organizationId}/approval-lines")
    ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody ApprovalLineDTO approvalLineDTO);


    /**
     * 删除申请行
     *
     * @param tenantId
     * @param approvalLineDTO
     * @return
     */
    @DeleteMapping("/v1/{organizationId}/approval-lines")
    ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                             @RequestBody ApprovalLineDTO approvalLineDTO);
}
