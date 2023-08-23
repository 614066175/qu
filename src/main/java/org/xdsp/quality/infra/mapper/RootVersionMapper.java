package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.domain.entity.RootVersion;

import java.util.List;

/**
 * 词根版本Mapper
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
public interface RootVersionMapper extends BaseMapper<RootVersion> {

    List<RootVersion> list(RootVersion rootVersion);

    RootVersion detail(Long id);

}
