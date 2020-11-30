package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>标准文档管理表资源库</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:40:10
 */
public interface StandardDocRepository extends BaseRepository<StandardDoc, StandardDocDTO>, ProxySelf<StandardDocRepository> {

}