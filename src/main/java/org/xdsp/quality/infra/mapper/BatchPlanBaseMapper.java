package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.BatchPlanBaseDTO;
import org.xdsp.quality.domain.entity.BatchPlanBase;

import java.util.List;

/**
 * <p>批数据方案-基础配置表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanBaseMapper extends BaseMapper<BatchPlanBase> {

    /**
     * 评估方案维度查询质检项
     *
     * @param batchPlanBaseDTO 查询条件
     * @return List<BatchPlanBaseDTO> 实体类list
     */
    List<BatchPlanBaseDTO> list(BatchPlanBaseDTO batchPlanBaseDTO);

    /**
     * 查询明细
     *
     * @param planBaseId 查询条件
     * @return BatchPlanBaseDTO 实体类
     */
    BatchPlanBaseDTO detail(Long planBaseId);

    /**
     * 查询方案下的质检项，无需考虑动态表单,供质量评估使用
     *
     * @param batchPlanBaseDTO
     * @return
     */
    List<BatchPlanBase> execBaseList(BatchPlanBaseDTO batchPlanBaseDTO);

    /**
     * 根据质检项Id删除
     * @param planBaseIds
     */
    void deleteTableCon(List<Long> planBaseIds);

    /**
     * 根据质检项Id删除
     * @param planBaseIds
     */
    void deleteTableLine(List<Long> planBaseIds);

    /**
     * 根据质检项Id删除
     * @param planBaseIds
     */
    void deleteTable(List<Long> planBaseIds);

    /**
     * 根据质检项Id删除
     * @param planBaseIds
     */
    void deleteFieldCon(List<Long> planBaseIds);

    /**
     * 根据质检项Id删除
     * @param planBaseIds
     */
    void deleteFieldLine(List<Long> planBaseIds);

    /**
     * 根据质检项Id删除
     * @param planBaseIds
     */
    void deleteField(List<Long> planBaseIds);

    /**
     * 根据质检项Id删除
     * @param planBaseIds
     */
    void deleteTableRel(List<Long> planBaseIds);
}
