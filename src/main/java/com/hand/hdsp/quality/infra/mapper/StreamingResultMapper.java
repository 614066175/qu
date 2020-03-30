package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>实时数据方案结果表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultMapper extends BaseMapper<StreamingResult> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param streamingResultDTO
     * @return
     */
    List<StreamingResultDTO> listByGroup(StreamingResultDTO streamingResultDTO);

    /**
     *  查看方案评估报告
     *
     * @param streamingResultDTO
     * @return
     */
    List<StreamingResultDTO> showReport(StreamingResultDTO streamingResultDTO);

}
