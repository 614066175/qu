package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.PlanBaseAssignDTO;

import java.util.List;

/**
 * <p>质检项分配表应用服务</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
public interface PlanBaseAssignService {

    /**
     * 质检项分配
     *
     * @param planBaseAssignDTOList
     * @return
     */
    List<PlanBaseAssignDTO> baseAssign(List<PlanBaseAssignDTO> planBaseAssignDTOList);
}
