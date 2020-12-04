package com.hand.hdsp.quality.domain.repository;

import java.util.List;

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

    /**
     * 查看执行结果（最新的执行历史）
     *
     * @param standardId 标准ID
     * @return NameExecHistoryDTO
     */
    NameExecHistoryDTO getLatestHistory(Long standardId);

    /**
     * 查看执行结果（最新的执行历史）
     *
     * @param standardId 标准ID
     * @return List<NameExecHistoryDTO>
     */
    List<NameExecHistoryDTO> getHistoryList(Long standardId);

    /**
     * 执行历史明细
     *
     * @param historyId 执行历史ID
     * @return NameExecHistoryDTO
     */
    NameExecHistoryDTO detail(Long historyId);
}