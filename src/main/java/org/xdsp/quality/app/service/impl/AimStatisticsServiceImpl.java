package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.xdsp.quality.api.dto.AimStatisticsDTO;
import org.xdsp.quality.app.service.AimStatisticsService;
import org.xdsp.quality.domain.repository.AimStatisticsRepository;
import org.xdsp.quality.infra.mapper.AimStatisticsMapper;

import java.math.BigDecimal;

/**
 * <p>标准落标统计表应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Service
public class AimStatisticsServiceImpl implements AimStatisticsService {
    private final AimStatisticsMapper aimStatisticsMapper;
    private final AimStatisticsRepository aimStatisticsRepository;

    public AimStatisticsServiceImpl(AimStatisticsMapper aimStatisticsMapper,
                                    AimStatisticsRepository aimStatisticsRepository) {
        this.aimStatisticsMapper = aimStatisticsMapper;
        this.aimStatisticsRepository = aimStatisticsRepository;
    }

    @Override
    public Page<AimStatisticsDTO> list(PageRequest pageRequest, AimStatisticsDTO aimStatisticsDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> aimStatisticsMapper.list(aimStatisticsDTO));
    }

    @Override
    public AimStatisticsDTO totalStatistic(Long tenantId, AimStatisticsDTO aimStatisticsDTO) {
        AimStatisticsDTO totalStatistic = aimStatisticsMapper.totalStatistic(aimStatisticsDTO);
        if (totalStatistic == null) {
            return new AimStatisticsDTO();
        }
        totalStatistic.setTotalCompliantRate(getPercent(totalStatistic.getTotalCompliantRow(), totalStatistic.getTotalRowNum()));
        totalStatistic.setTotalAcompliantRate(getPercent(totalStatistic.getTotalCompliantRow(), totalStatistic.getTotalNonNullRow()));
        return totalStatistic;
    }

    private BigDecimal getPercent(Long num1, Long num2) {
        if (num2 == 0) {
            return BigDecimal.valueOf(0);
        } else {
            return BigDecimal.valueOf(num1).divide(BigDecimal.valueOf(num2), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
