package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.LocValueDTO;
import org.xdsp.quality.domain.entity.LocValue;

import java.util.List;

/**
 * <p>loc独立值集表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocValueMapper extends BaseMapper<LocValue> {



    /**
     * 行表模糊查询
     *
     * @param locValueDTO
     * @return
     */
    List<LocValueDTO> queryList( LocValueDTO locValueDTO);
}
