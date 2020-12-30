package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.ResultObjDTO;

/**
 * <p>批数据方案结果表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface BatchResultService {

    /**
     * 查看运行日志
     *
     * @param tenantId
     * @param execId
     * @param jobId
     * @return
     */
    ResultObjDTO showLog(Long tenantId, int execId, String jobId);

    /**
     * 评估方案的评估信息
     * @param batchResultDTO
     * @return
     */
    BatchResultDTO listResultDetail(BatchResultDTO batchResultDTO);

    /**
     * 评估方案评估详情-异常数据
     * @param batchResultDTO
     * @return
     */
    BatchResultDTO listExceptionDetail(BatchResultDTO batchResultDTO);
}
