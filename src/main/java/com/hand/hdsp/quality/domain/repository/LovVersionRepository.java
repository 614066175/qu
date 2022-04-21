package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovVersion;

import java.util.List;

/**
 * <p>LOV表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovVersionRepository extends BaseRepository<LovVersion, LovVersionDTO>, ProxySelf<LovVersionRepository> {

    /**
     * 获取lovId下的版本列表
     * @param lovId
     * @return
     */
    List<CodeVersion> getCodeVersion(Long lovId);

    /**
     * 获取最大版本号列表
     * @param tenantId
     * @return
     */

    List<LovVersionDTO> getMaxVersionList(Long tenantId);
}