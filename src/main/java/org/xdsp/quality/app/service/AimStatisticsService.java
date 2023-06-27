package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.quality.api.dto.AimStatisticsDTO;

/**
 * <p>标准落标统计表应用服务</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
public interface AimStatisticsService {

    /**
     * 落标统计列表查询
     *
     * @param pageRequest
     * @param aimStatisticsDTO
     * @return
     */
    Page<AimStatisticsDTO> list(PageRequest pageRequest, AimStatisticsDTO aimStatisticsDTO);

    /**
     * 落标统计总计
     * @param tenantId
     * @param aimStatisticsDTO
     * @return
     */
    AimStatisticsDTO totalStatistic(Long tenantId, AimStatisticsDTO aimStatisticsDTO);
}
