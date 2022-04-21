package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.domain.entity.LovValue;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>LOV独立值集表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovValueMapper extends BaseMapper<LovValue> {

    /**
     * 根据lovId和查询字符进行模糊查询
     * @param lovId
     * @param query
     * @return
     */
    List<LovValueDTO> getLovValueDTOS(Long lovId, String query);
}
