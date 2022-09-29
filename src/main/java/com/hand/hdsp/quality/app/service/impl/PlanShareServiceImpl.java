package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.PlanShareDTO;
import com.hand.hdsp.quality.app.service.PlanShareService;
import com.hand.hdsp.quality.infra.mapper.PlanShareMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Service
public class PlanShareServiceImpl implements PlanShareService {

    private final PlanShareMapper planShareMapper;

    public PlanShareServiceImpl(PlanShareMapper planShareMapper) {
        this.planShareMapper = planShareMapper;
    }

    @Override
    public List<PlanShareDTO> shareProjects(Long planId) {
        //查询方案共享的项目
        return  planShareMapper.shareProjects(planId);
    }
}
