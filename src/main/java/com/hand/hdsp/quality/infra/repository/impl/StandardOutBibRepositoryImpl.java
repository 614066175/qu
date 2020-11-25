package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardOutBibDTO;
import com.hand.hdsp.quality.domain.entity.StandardOutBib;
import com.hand.hdsp.quality.domain.repository.StandardOutBibRepository;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 11:30
 * @since 1.0
 */
@Component
public class StandardOutBibRepositoryImpl extends BaseRepositoryImpl<StandardOutBib, StandardOutBibDTO> implements StandardOutBibRepository {

}
