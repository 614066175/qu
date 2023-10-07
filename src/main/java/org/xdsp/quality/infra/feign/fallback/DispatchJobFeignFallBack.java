package org.xdsp.quality.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.JobDTO;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.feign.DispatchJobFeign;

/**
 * description
 *
 * @author lei.xie03@hand-china.com 2019/05/31 14:58
 */
@Slf4j
@Component
public class DispatchJobFeignFallBack implements DispatchJobFeign {

    /**
     * 创建或者更新
     *
     * @param tenantId 租户id
     * @param jobDTO   job
     * @return job
     */
    @Override
    public ResponseEntity<String> createOrUpdate(Long tenantId, Long projectId, JobDTO jobDTO) {
        log.error("feign调用job createOrUpdate错误, jobDTO: {}", jobDTO);
        throw new CommonException(ErrorCode.JOB_CREATE_UPDATE);
    }

    @Override
    public ResponseEntity<String> findByCode(Long tenantId, Long projectId, String jobCode) {
        throw new CommonException("查找质量任务失败！");
    }

}
