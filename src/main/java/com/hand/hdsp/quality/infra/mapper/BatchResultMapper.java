package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>批数据方案结果表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface BatchResultMapper extends BaseMapper<BatchResult> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    List<BatchResultDTO> listByGroup(BatchResultDTO batchResultDTO);

    /**
     * 根据分组查询对应的评估方案执行记录
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    List<BatchResultDTO> listHistory(BatchResultDTO batchResultDTO);
}
