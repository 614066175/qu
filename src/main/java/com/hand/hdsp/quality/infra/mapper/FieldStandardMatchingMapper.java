package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatching;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>字段标准匹配表Mapper</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
public interface FieldStandardMatchingMapper extends BaseMapper<FieldStandardMatching> {

    /**
     * 查找字段标准匹配集合
     * @param fieldStandardMatchingDTO 查询条件
     * @return 字段标准匹配集合
     */
    List<FieldStandardMatchingDTO> findAllMatchings(FieldStandardMatchingDTO fieldStandardMatchingDTO);
}
