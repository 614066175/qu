package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>实时数据方案结果表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultRepository extends BaseRepository<StreamingResult, StreamingResultDTO>, ProxySelf<StreamingResultRepository> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param streamingResultDTO
     * @param pageRequest
     * @return StreamingResultDTO
     */
    Page<StreamingResultDTO> listAll(StreamingResultDTO streamingResultDTO, PageRequest pageRequest);

    /**
     * 查看方案评估报告
     *
     * @param streamingResultDTO
     * @return StreamingResultDTO
     */
    StreamingResultDTO showReport(StreamingResultDTO streamingResultDTO);

    /**
     * 根据分组查询对应的评估方案执行记录
     *
     * @param streamingResultDTO
     * @param pageRequest
     * @return StreamingResultDTO
     */
    Page<StreamingResultDTO> listHistory(StreamingResultDTO streamingResultDTO, PageRequest pageRequest);
}
