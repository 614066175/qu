package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>批数据方案结果表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface BatchResultRepository extends BaseRepository<BatchResult, BatchResultDTO>, ProxySelf<BatchResultRepository> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param batchResultDTO
     * @param pageRequest
     * @return BatchResultDTO
     */
    Page<BatchResultDTO> listAll(BatchResultDTO batchResultDTO, PageRequest pageRequest);

    /**
     * 查看方法评估报告
     *
     * @param batchResultDTO
     * @return
     */
    BatchResultDTO showReport(BatchResultDTO batchResultDTO);
}
