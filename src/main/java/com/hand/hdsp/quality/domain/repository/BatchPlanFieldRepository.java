package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>批数据方案-字段规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldRepository extends BaseRepository<BatchPlanField, BatchPlanFieldDTO>, ProxySelf<BatchPlanFieldRepository> {

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByParentId(Long planBaseId);

    /**
     * 去重列表
     *
     * @param pageRequest
     * @param batchPlanFieldDTO
     * @return
     */
    Page<BatchPlanFieldDTO> distinctPageAndSortDTO(PageRequest pageRequest, BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 列表
     *
     * @param batchPlanField
     * @return
     */
    List<BatchPlanField> list(BatchPlanField batchPlanField);
}
