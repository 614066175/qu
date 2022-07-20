package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.AimStatistics;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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