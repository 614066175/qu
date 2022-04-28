package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.domain.entity.LocVersion;
import com.hand.hdsp.quality.domain.repository.LocVersionRepository;
import com.hand.hdsp.quality.infra.mapper.LocVersionMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>loc表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocVersionRepositoryImpl extends BaseRepositoryImpl<LocVersion, LocVersionDTO> implements LocVersionRepository {
    
}
