package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.RootDicDTO;
import org.xdsp.quality.domain.entity.RootDic;
import org.xdsp.quality.domain.repository.RootDicRepository;

/**
 * <p>资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
@Component
public class RootDicRepositoryImpl extends BaseRepositoryImpl<RootDic, RootDicDTO> implements RootDicRepository {

}
