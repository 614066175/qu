package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.LocDTO;
import org.xdsp.quality.domain.entity.Loc;

import java.util.List;

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

    /**
     * 头表模糊查询
     * @param locDTO
     * @return
     */

    List<LocDTO> list(LocDTO locDTO);
}
