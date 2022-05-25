package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import io.choerodon.mybatis.common.BaseMapper;

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
     * 分组维度查询质检项 （查询此分组下所有方案的所有质检项）
     *
     * @param batchPlanBaseDTO
     * @return
     */
    List<BatchPlanBaseDTO> groupList(BatchPlanBaseDTO batchPlanBaseDTO);
}
