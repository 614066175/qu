package org.xdsp.quality.infra.feign.fallback;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.DatasourceDTO;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.feign.DatasourceFeign;


/**
 * 数据源 Fallback
 *
 * @author feng.liu01@hand-china.com 2020-02-03 14:40:55
 */
@Component
public class DatasourceFeignFallback implements DatasourceFeign {
    @Override
    public Page<DatasourceDTO> list(Long tenantId, String datasourceName) {
        throw new CommonException(ErrorCode.CORE_DATASOURCE_LIST);
    }

    @Override
    public ResponseEntity<String> execSql(Long tenantId, DatasourceDTO datasourceDTO) {
        throw new CommonException(ErrorCode.CORE_DATASOURCE_EXEC_SQL);
    }

    @Override
    public ResponseEntity<String> execQuery(Long tenantId, DatasourceDTO datasourceDTO) {
        throw new CommonException(ErrorCode.CORE_DATASOURCE_EXEC_QUERY);
    }

    @Override
    public ResponseEntity<String> execSqlBatch(Long tenantId, DatasourceDTO datasourceDTO) {
        throw new CommonException(ErrorCode.CORE_DATASOURCE_EXEC_SQL_BATCH);
    }

    @Override
    public ResponseEntity<String> detail(Long tenantId, Long datasourceId) {
        throw new CommonException(ErrorCode.CORE_DATASOURCE_DETAIL);
    }
}
