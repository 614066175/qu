package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.domain.entity.LocVersion;
import io.choerodon.mybatis.common.BaseMapper;

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

    /**
     * 获取更新人账户名
     * @param lastUpdatedBy
     * @return
     */
    String getUpdateUserName(Long lastUpdatedBy);
}

