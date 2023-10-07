package org.xdsp.quality.app.service;

import org.hzero.export.vo.ExportParam;
import org.xdsp.quality.api.dto.PlanGroupDTO;

import java.util.List;

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
    List<PlanGroupDTO> export(PlanGroupDTO dto, ExportParam exportParam);


    /**
     * 新建分组
     * @param dto
     * @return
     */
    int create(PlanGroupDTO dto);
}
