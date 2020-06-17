package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.BatchResultItemDO;
import com.hand.hdsp.quality.infra.dataobject.BatchResultRuleDO;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>批数据方案结果表-校验项信息Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
public interface BatchResultItemMapper extends BaseMapper<BatchResultItem> {


    /**
     * 查询历史实际值
     *
     * @param batchResultItem
     * @return
     */
    List<BatchResultItemDO> queryList(BatchResultItemDO batchResultItem);

    /**
     * 根据结果id查询出所有数据
     *
     * @param resultId
     * @return
     */
    List<BatchResultItemDTO> selectByResultId(Long resultId);

    /**
     * 查出各个告警等级数
     *
     * @param batchResultRule
     * @return
     */
    List<ResultWaringVO> selectWaringLevel(BatchResultRuleDO batchResultRule);

    /**
     * 根据结果id查询出告警等级并去重
     *
     * @param resultId
     * @return
     */
    List<String> selectWaringLevelByResultId(Long resultId);
}