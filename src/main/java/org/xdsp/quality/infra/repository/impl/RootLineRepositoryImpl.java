package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.RootLineDTO;
import org.xdsp.quality.domain.entity.RootLine;
import org.xdsp.quality.domain.repository.RootLineRepository;

/**
 * 词根中文名行表 资源库实现
 *
 * @author xin.sheng01@china-hand.com 2022-11-22 15:37:15
 */
@Component
public class RootLineRepositoryImpl extends BaseRepositoryImpl<RootLine, RootLineDTO> implements RootLineRepository {

  
}
