package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>批数据方案结果表-校验项信息资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
public interface BatchResultItemRepository extends BaseRepository<BatchResultItem, BatchResultItemDTO>, ProxySelf<BatchResultItemRepository> {


    /**
     * 根据结果id查询出所有数据
     *
     * @param resultId
     * @return
     */
    List<BatchResultItemDTO> selectByResultId(Long resultId);


    /**
     * 根据结果id查询出告警等级并去重
     *
     * @param resultId
     * @return
     */
    List<String> selectWaringLevelByResultId(Long resultId);

    /**
     * 评估结果各级规则错误信息
     *
     * @param pageRequest
     * @param batchResultItemDTO
     * @return
     */
    Page<BatchResultItemDTO> listRuleError(PageRequest pageRequest, BatchResultItemDTO batchResultItemDTO);
}
