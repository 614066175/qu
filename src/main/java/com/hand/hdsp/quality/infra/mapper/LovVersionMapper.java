package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovVersion;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>LOV表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovVersionMapper extends BaseMapper<LovVersion> {

    /**
     * 获取历史值集版本
     * @param lovId
     * @return
     */
    List<CodeVersion> getCodeVersin(Long lovId);

    /**
     * 获取最大版本的头表
     * @param tenantId
     * @return
     */
    List<LovVersionDTO> getMaxVersionList(Long tenantId);
}

