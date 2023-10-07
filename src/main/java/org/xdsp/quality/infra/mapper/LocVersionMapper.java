package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.LocVersionDTO;
import org.xdsp.quality.domain.entity.LocVersion;

import java.util.List;

/**
 * <p>loc表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocVersionMapper extends BaseMapper<LocVersion> {

    /**
     * 获取历史版本表
     * @param locVersionDTO
     * @return
     */
    List<LocVersionDTO> listAll(LocVersionDTO locVersionDTO);


}

