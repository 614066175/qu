package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.app.service.AimStatisticsService;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * <p>标准落标统计表应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Service
public class AimStatisticsServiceImpl implements AimStatisticsService {

    @Override
    public Page<AimStatisticsDTO> list(PageRequest pageRequest, AimStatisticsDTO aimStatisticsDTO) {
        return null;
    }
}
