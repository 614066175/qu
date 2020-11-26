package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameStandardContentExclude;
import com.hand.hdsp.quality.api.dto.NameStandardContentExcludeDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardContentExcludeRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准落标排除表资源库实现</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardContentExcludeRepositoryImpl extends BaseRepositoryImpl<NameStandardContentExclude, NameStandardContentExcludeDTO> implements NameStandardContentExcludeRepository {

}
