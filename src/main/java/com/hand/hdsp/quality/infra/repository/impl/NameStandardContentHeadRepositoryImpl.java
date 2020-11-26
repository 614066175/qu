package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameStandardContentHead;
import com.hand.hdsp.quality.api.dto.NameStandardContentHeadDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardContentHeadRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准落标头表资源库实现</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardContentHeadRepositoryImpl extends BaseRepositoryImpl<NameStandardContentHead, NameStandardContentHeadDTO> implements NameStandardContentHeadRepository {

}
