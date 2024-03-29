package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.BatchResultBaseDTO;
import org.xdsp.quality.api.dto.BatchResultItemDTO;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.infra.dataobject.BatchResultItemDO;
import org.xdsp.quality.infra.vo.ResultWaringVO;

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

    /**
     * 查询每个校验项的告警等级json
     *
     * @param batchResultBaseDTO
     * @return
     */
    List<String> selectWaringLevelJson(BatchResultBaseDTO batchResultBaseDTO);

    /**
     * 数据标准告警查询
     * @param batchResultBaseDTO
     * @return
     */
    List<String> dataStandardWaringLevelVO(BatchResultBaseDTO batchResultBaseDTO);

    /**
     * 根据planBaseId查询最大结果值
     * @param planBaseId 查询条件
     * @param projectId
     * @return 返回值
     */
    Long selectMaxResultBaseId(@Param("planBaseId") Long planBaseId, @Param("projectId") Long projectId);
}
