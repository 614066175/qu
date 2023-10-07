package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.SuggestDTO;
import org.xdsp.quality.domain.entity.Suggest;
import org.xdsp.quality.domain.repository.SuggestRepository;

/**
 * <p>问题知识库表资源库实现</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Component
public class SuggestRepositoryImpl extends BaseRepositoryImpl<Suggest, SuggestDTO> implements SuggestRepository {

}
