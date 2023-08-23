package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.SuggestDTO;
import org.xdsp.quality.domain.entity.Suggest;

/**
 * <p>问题知识库表资源库</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
public interface SuggestRepository extends BaseRepository<Suggest, SuggestDTO>, ProxySelf<SuggestRepository> {

}