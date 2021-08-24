package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.Suggest;
import com.hand.hdsp.quality.api.dto.SuggestDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>问题知识库表资源库</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
public interface SuggestRepository extends BaseRepository<Suggest, SuggestDTO>, ProxySelf<SuggestRepository> {

}