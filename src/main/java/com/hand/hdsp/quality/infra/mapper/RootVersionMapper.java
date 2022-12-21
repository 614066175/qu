package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.RootVersion;
import jdk.nashorn.internal.runtime.Version;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 词根版本Mapper
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
public interface RootVersionMapper extends BaseMapper<RootVersion> {

    List<RootVersion> list(RootVersion rootVersion);

    RootVersion detail(Long id);

}
