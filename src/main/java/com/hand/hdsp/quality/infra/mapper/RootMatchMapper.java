package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RootMatchDTO;
import com.hand.hdsp.quality.domain.entity.RootMatch;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>字段标准匹配表Mapper</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
public interface RootMatchMapper extends BaseMapper<RootMatch> {

    /**
     * 查找字段标准匹配集合
     * @param rootMatchDTO 查询条件
     * @return 字段标准匹配集合
     */
    List<RootMatchDTO> findAllMatch(RootMatchDTO rootMatchDTO);

}
