package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.xdsp.quality.app.service.RootVersionService;
import org.xdsp.quality.domain.entity.RootVersion;
import org.xdsp.quality.domain.repository.RootVersionRepository;

/**
 * 词根版本应用服务默认实现
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@Service
public class RootVersionServiceImpl implements RootVersionService {

    private final RootVersionRepository rootVersionRepository;

    public RootVersionServiceImpl(RootVersionRepository rootVersionRepository) {
        this.rootVersionRepository = rootVersionRepository;
    }

    @Override
    public Page<RootVersion> list(PageRequest pageRequest, RootVersion rootVersion) {
        return PageHelper.doPageAndSort(pageRequest,()->rootVersionRepository.list(rootVersion));
    }
}
