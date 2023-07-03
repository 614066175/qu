package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.AimStatisticsDTO;
import org.xdsp.quality.domain.entity.AimStatistics;
import org.xdsp.quality.domain.repository.AimStatisticsRepository;

/**
 * <p>标准落标统计表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Component
public class AimStatisticsRepositoryImpl extends BaseRepositoryImpl<AimStatistics, AimStatisticsDTO> implements AimStatisticsRepository {

    @Override
    public Page<AimStatisticsDTO> list(PageRequest pageRequest, AimStatisticsDTO aimStatisticsDTO) {
        return null;
    }
}
