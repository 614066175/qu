package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.BatchResultItemDO;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
     * 根据结果id查询出告警等级并去重
     *
     * @param resultId
     * @return
     */
    List<String> selectWaringLevelByResultId(Long resultId);

    /**
     * 评估结果各级规则错误信息
     *
     * @param batchResultItemDTO
     * @return
     */
    List<BatchResultItemDTO> listRuleError(BatchResultItemDTO batchResultItemDTO);

    /**
     * 关联asset_table表
     *
     * @param batchResultItemDTO 实体类
     * @return List<BatchResultItemDTO> list
     */
    List<BatchResultItemDTO> assetTable(BatchResultItemDTO batchResultItemDTO);

    /**
     * 查询每个告警等级的数量
     *
     * @param batchResultBaseDTO
     * @return
     */
    List<ResultWaringVO> selectWaringLevel(BatchResultBaseDTO batchResultBaseDTO);

    /**
     * 根据planI查询告警等级
     * @param resultId
     * @return
     */
    List<ResultWaringVO> selectWarningLevelByResultId(@Param("resultId") Long resultId);
}
