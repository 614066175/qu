package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.app.service.RootVersionService;
import com.hand.hdsp.quality.domain.entity.RootVersion;
import com.hand.hdsp.quality.domain.repository.RootVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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
