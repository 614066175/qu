package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.infra.vo.PlanGroupTreeVO;

import java.util.List;

/**
 * <p>评估方案分组表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanGroupMapper extends BaseMapper<PlanGroup> {

    /**
     * 含批数据评估方案列表
     *
     * @param planGroupTreeVO 查询条件
     * @return
     */
    List<PlanGroupTreeVO> treeBatch(PlanGroupTreeVO planGroupTreeVO);

    /**
     * 含实时数据评估方案列表
     *
     * @param planGroupTreeVO 查询条件
     * @return
     */
    List<PlanGroupTreeVO> treeStream(PlanGroupTreeVO planGroupTreeVO);


    /**
     * 查询自己的和被分享的分组
     * @return
     * @param planGroup
     */
    List<PlanGroup> ownAndShareGroup(PlanGroup planGroup);

    /**
     * 获取父分组信息，包含父分组的父分组编码
     * @param parentGroupId
     * @return
     */
    PlanGroupDTO selectWithParentCode(Long parentGroupId);
}
