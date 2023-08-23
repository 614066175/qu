package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.RootDicDTO;
import org.xdsp.quality.domain.entity.RootDic;

/**
 * <p>资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
public interface RootDicRepository extends BaseRepository<RootDic, RootDicDTO>, ProxySelf<RootDicRepository> {

}