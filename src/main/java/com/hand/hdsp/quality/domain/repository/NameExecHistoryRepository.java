package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.api.dto.NameExecHistoryDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准执行历史表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameExecHistoryRepository extends BaseRepository<NameExecHistory, NameExecHistoryDTO>, ProxySelf<NameExecHistoryRepository> {

}