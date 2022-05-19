package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;
import java.util.Map;

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
     *
     * @param batchResultDTO
     * @return
     */
    BatchResultDTO listResultDetail(BatchResultDTO batchResultDTO);

    /**
     * 评估方案评估详情-异常数据
     *
     * @param resultId Long
     * @param tenantId Long
     * @param pageRequest PageRequest
     * @return
     */
    Page<BatchResultBaseDTO> listExceptionDetailHead(Long resultId, Long tenantId, PageRequest pageRequest);

    /**
     * @param exceptionDataDTO
     * @param pageRequest
     * @return List<Map < String, Object>>
     */
    Page<Map> listExceptionDetail(ExceptionDataDTO exceptionDataDTO, PageRequest pageRequest);

    /**
     * 使用sql查询问题趋势
     * @param timeRangeDTO 查询条件
     * @return 返回值
     */
    List<TimeRangeDTO> listProblemData(TimeRangeDTO timeRangeDTO);
}
