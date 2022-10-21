package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.PlanShareDTO;
import com.hand.hdsp.quality.domain.entity.PlanShare;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>Mapper</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
public interface PlanShareMapper extends BaseMapper<PlanShare> {

    /**
     * 查询方案共享的项目
     * @param planId
     * @return
     */
    List<PlanShareDTO> shareProjects(Long planId);
}
