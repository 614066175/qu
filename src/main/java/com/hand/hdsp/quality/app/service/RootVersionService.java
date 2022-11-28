package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.domain.entity.RootVersion;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 词根版本应用服务
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
public interface RootVersionService {
    Page<RootVersion> list(PageRequest pageRequest, RootVersion rootVersion);
}
