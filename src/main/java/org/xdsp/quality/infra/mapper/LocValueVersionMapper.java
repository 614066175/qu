package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.LocValueVersionDTO;
import org.xdsp.quality.domain.entity.LocValueVersion;

import java.util.List;

/**
 * <p>loc独立值集表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
public interface LocValueVersionMapper extends BaseMapper<LocValueVersion> {


    /**
     * 查询历史行表
     * @param locValueVersionDTO
     * @return
     */
    List<LocValueVersionDTO> getList( LocValueVersionDTO locValueVersionDTO);
}
