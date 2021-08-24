package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.Suggest;
import com.hand.hdsp.quality.api.dto.SuggestDTO;
import com.hand.hdsp.quality.domain.repository.SuggestRepository;
import org.springframework.stereotype.Component;

/**
 * <p>问题知识库表资源库实现</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Component
public class SuggestRepositoryImpl extends BaseRepositoryImpl<Suggest, SuggestDTO> implements SuggestRepository {

}
