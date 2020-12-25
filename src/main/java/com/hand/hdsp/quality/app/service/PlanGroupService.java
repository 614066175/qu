package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.infra.vo.PlanGroupVO;
import org.hzero.export.vo.ExportParam;

/**
 * <p>评估方案分组表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanGroupService {

    /**
     * 删除
     *
     * @param planGroupDTO 删除条件
     * @return 删除结果
     */
    int delete(PlanGroupDTO planGroupDTO);

    /**
     * 导出评估方案
     * @param dto
     * @param exportParam
     * @return
     */
    List<PlanGroupDTO> export(PlanGroupVO dto, ExportParam exportParam);
}
