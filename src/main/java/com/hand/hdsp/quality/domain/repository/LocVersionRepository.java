package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.domain.entity.LocVersion;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>loc表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocVersionRepository extends BaseRepository<LocVersion, LocVersionDTO>, ProxySelf<LocVersionRepository> {


}