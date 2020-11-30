package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准文档管理表资源库实现</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Component
public class StandardDocRepositoryImpl extends BaseRepositoryImpl<StandardDoc, StandardDocDTO> implements StandardDocRepository {
}
