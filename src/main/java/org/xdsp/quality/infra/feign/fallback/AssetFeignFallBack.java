package org.xdsp.quality.infra.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.infra.feign.AssetFeign;

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

    @Override
    public ResponseEntity<?> saveFieldToEs(Long tenantId, DataFieldDTO dataFieldDTO) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteFieldToEs(Long tenantId, DataFieldDTO dataFieldDTO) {
        return null;
    }
}
