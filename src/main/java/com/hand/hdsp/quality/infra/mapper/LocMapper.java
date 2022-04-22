package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.LocDTO;
import com.hand.hdsp.quality.domain.entity.Loc;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>loc表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocMapper extends BaseMapper<Loc> {

    /**
     * 详情接口
     *
     * @param locId
     * @return
     */
    LocDTO detail(Long locId);
}
