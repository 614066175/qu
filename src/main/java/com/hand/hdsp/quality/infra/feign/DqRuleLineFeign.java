package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.quality.api.dto.DqRuleLineDTO;
import com.hand.hdsp.quality.infra.feign.fallback.DqRuleLineFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ops 数据质量监控规则表 feign
 *
 * @author feng.liu01@hand-china.com 2020-04-20 20:03:59
 */
@FeignClient(
        value = "${hdsp.service.ops:hdsp-ops}",
        fallback = DqRuleLineFeignFallback.class
)
public interface DqRuleLineFeign {
    /**
     * 数据质量监控规则表列表（不分页，feign调用）
     *
     * @param tenantId
     * @param dqRuleLineDTO
     * @return
     */
    @PostMapping("/v1/{organizationId}/dq-rule-lines/listAll")
    ResponseEntity<String> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                   @RequestBody DqRuleLineDTO dqRuleLineDTO);
}
