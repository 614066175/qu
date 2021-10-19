package com.hand.hdsp.quality.infra.feign;

import com.hand.hdsp.quality.api.dto.LineageDTO;
import com.hand.hdsp.quality.infra.feign.fallback.LineageFeignFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/10/18 16:25
 * @since 1.0
 */
@FeignClient(
        value = "${hdsp.service.lineage:hdsp-lineage}",
        fallback = LineageFeignFallBack.class
)
public interface LineageFeign {

    @PostMapping("/v1/{organizationId}/table/update-lineage-status")
    ResponseEntity<List<LineageDTO>> updateLineageStatus(@PathVariable(name = "organizationId") Long tenantId,
                                                         @RequestBody List<LineageDTO> lineageDTOS);
}
