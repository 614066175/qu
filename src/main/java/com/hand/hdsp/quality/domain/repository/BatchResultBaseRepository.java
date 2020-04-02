package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;

/**
 * <p>批数据方案结果表-表信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchResultBaseRepository extends BaseRepository<BatchResultBase, BatchResultBaseDTO>, ProxySelf<BatchResultBaseRepository> {

    /**
     * 查看评估报告结果base表信息
     *
     * @param batchResultBaseDTO
     * @return
     */
    List<BatchResultBaseDTO> listResultBase(BatchResultBaseDTO batchResultBaseDTO);
}
