package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameStandardContentLine;
import com.hand.hdsp.quality.api.dto.NameStandardContentLineDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardContentLineRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准落标行表资源库实现</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardContentLineRepositoryImpl extends BaseRepositoryImpl<NameStandardContentLine, NameStandardContentLineDTO> implements NameStandardContentLineRepository {

}
