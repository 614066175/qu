package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RootDicDTO;
import com.hand.hdsp.quality.domain.entity.RootDic;

/**
 * <p>资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
public interface RootDicRepository extends BaseRepository<RootDic, RootDicDTO>, ProxySelf<RootDicRepository> {

}