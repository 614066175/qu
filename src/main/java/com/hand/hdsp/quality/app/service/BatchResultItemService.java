package com.hand.hdsp.quality.app.service;

import java.util.Map;

import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;

/**
 * <p>批数据方案结果表-校验项信息应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
public interface BatchResultItemService {

    Map<String, Map<String,Long>> analysisQuality(BatchResultItemDTO batchResultItemDTO);
}
