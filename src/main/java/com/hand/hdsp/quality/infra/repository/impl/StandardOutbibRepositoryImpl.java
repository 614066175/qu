package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardOutbibDTO;
import com.hand.hdsp.quality.domain.entity.StandardOutbib;
import com.hand.hdsp.quality.domain.repository.StandardOutbibRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准落标表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 17:44:46
 */
@Component
public class StandardOutbibRepositoryImpl extends BaseRepositoryImpl<StandardOutbib, StandardOutbibDTO> implements StandardOutbibRepository {

}
