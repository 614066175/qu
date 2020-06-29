package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StreamingResultBaseDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResultBase;

import java.util.List;

/**
 * <p>实时数据方案结果表-基础信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultBaseRepository extends BaseRepository<StreamingResultBase, StreamingResultBaseDTO>, ProxySelf<StreamingResultBaseRepository> {

    /**
     * 查看评估报告base结果表
     *
     * @param streamingResultBaseDTO
     * @return StreamingResultBaseDTO
     */
    List<StreamingResultBaseDTO> listResultBase(StreamingResultBaseDTO streamingResultBaseDTO);
}
