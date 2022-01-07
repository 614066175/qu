package com.hand.hdsp.quality.infra.feign.fallback;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.infra.feign.AssetFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/26 14:04
 * @since 1.0
 */
@Component
@Slf4j
public class AssetFeignFallBack implements AssetFeign {


    @Override
    public ResponseEntity<?> saveStandardToEs(Long tenantId, DataStandardDTO dataStandardDTO) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteStandardToEs(Long tenantId, DataStandardDTO dataStandardDTO) {
        return null;
    }
}
