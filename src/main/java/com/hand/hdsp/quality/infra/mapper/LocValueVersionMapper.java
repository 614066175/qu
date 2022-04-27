package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.LocValueVersionDTO;
import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.domain.entity.LocValueVersion;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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
