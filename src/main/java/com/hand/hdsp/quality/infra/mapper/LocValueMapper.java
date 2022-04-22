package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.LocValueDTO;
import com.hand.hdsp.quality.domain.entity.LocValue;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.ibatis.annotations.Param;

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
