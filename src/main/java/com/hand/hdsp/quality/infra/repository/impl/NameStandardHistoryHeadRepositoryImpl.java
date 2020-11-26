package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameStandardHistoryHead;
import com.hand.hdsp.quality.api.dto.NameStandardHistoryHeadDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardHistoryHeadRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准执行历史头表资源库实现</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardHistoryHeadRepositoryImpl extends BaseRepositoryImpl<NameStandardHistoryHead, NameStandardHistoryHeadDTO> implements NameStandardHistoryHeadRepository {

}
