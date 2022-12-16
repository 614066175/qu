package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RootMatchHisDTO;
import com.hand.hdsp.quality.domain.entity.RootMatchHis;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>字段标准匹配记录表Mapper</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
public interface RootMatchHisMapper extends BaseMapper<RootMatchHis> {
    /**
     * 字段标准匹配记录详情
     * @param rootMatchHisDTO 查询条件
     * @return 字段标准匹配记录集合详情
     */
    List<RootMatchHisDTO> listAll(RootMatchHisDTO rootMatchHisDTO);
}
