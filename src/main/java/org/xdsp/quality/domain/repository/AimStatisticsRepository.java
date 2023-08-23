package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.AimStatisticsDTO;
import org.xdsp.quality.domain.entity.AimStatistics;

/**
 * <p>标准落标统计表资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
public interface AimStatisticsRepository extends BaseRepository<AimStatistics, AimStatisticsDTO>, ProxySelf<AimStatisticsRepository> {

    /**
     * 列表查询落标统计
     *
     * @param pageRequest
     * @param aimStatisticsDTO
     * @return
     */
    Page<AimStatisticsDTO> list(PageRequest pageRequest, AimStatisticsDTO aimStatisticsDTO);
}