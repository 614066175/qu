package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.ResultObjDTO;
import com.hand.hdsp.quality.app.service.BatchResultService;
import com.hand.hdsp.quality.infra.feign.ExecutionFlowFeign;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * <p>批数据方案结果表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class BatchResultServiceImpl implements BatchResultService {

    private final ExecutionFlowFeign executionFlowFeign;

    public BatchResultServiceImpl(ExecutionFlowFeign executionFlowFeign) {
        this.executionFlowFeign = executionFlowFeign;
    }

    @Override
    public ResultObjDTO showLog(Long tenantId, int execId, String jobId) {
        ResponseEntity<String> result = executionFlowFeign.getJobLog(tenantId, execId, jobId);
        if (ResponseUtils.isFailed(result)){
            // 获取异常信息
            ExceptionResponse response = ResponseUtils.getResponse(result, ExceptionResponse.class);
            throw new CommonException(response.getMessage());
        }
        return ResponseUtils.getResponse(result,ResultObjDTO.class);
    }
}
