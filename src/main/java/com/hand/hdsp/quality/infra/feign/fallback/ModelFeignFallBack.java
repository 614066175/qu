package com.hand.hdsp.quality.infra.feign.fallback;

import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.feign.ModelFeign;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @Author: dongwl
 * @Date: 2022/06/22
 */
@Component
@Slf4j
public class ModelFeignFallBack implements ModelFeign {

    @Override
    public ResponseEntity<String> list(Long tenantId, Long projectId, Long customTableId) {
        throw new CommonException(ErrorCode.CORE_CUSTOMTABLE_COLUMN_LIST);
    }

    @Override
    public ResponseEntity<String> detailForFeign(Long tenantId, Long projectId, String customDatasourceType, String customDatasourceCode, String customSchemaName, String customTableName) {
        throw new CommonException(ErrorCode.CORE_CUSTOMTABLE_LIST);
    }
}
