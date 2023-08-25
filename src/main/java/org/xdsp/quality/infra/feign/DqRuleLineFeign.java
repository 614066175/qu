package org.xdsp.quality.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.xdsp.quality.api.dto.DqRuleLineDTO;
import org.xdsp.quality.infra.feign.fallback.DqRuleLineFeignFallback;

/**
 * ops 数据质量监控规则表 feign
 *
 * @author feng.liu01@hand-china.com 2020-04-20 20:03:59
 */
@FeignClient(
        value = "${xdsp.service.ops:xdsp-ops}",
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
