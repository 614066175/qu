package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.domain.entity.Lov;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>LOV表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovMapper extends BaseMapper<Lov> {

    /**
     * 详情接口
     * @param lovId
     * @return
     */
    LovDTO detail(Long lovId);
}
