package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.api.dto.NameExecHistoryDTO;
import com.hand.hdsp.quality.domain.repository.NameExecHistoryRepository;
import com.hand.hdsp.quality.infra.mapper.NameExecHistoryMapper;
import com.hand.hdsp.quality.infra.vo.NameStandardHisReportVO;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准执行历史表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameExecHistoryRepositoryImpl extends BaseRepositoryImpl<NameExecHistory, NameExecHistoryDTO> implements NameExecHistoryRepository {

    private final NameExecHistoryMapper nameExecHistoryMapper;

    public NameExecHistoryRepositoryImpl(NameExecHistoryMapper nameExecHistoryMapper) {
        this.nameExecHistoryMapper = nameExecHistoryMapper;
    }

    @Override
    public NameExecHistoryDTO getLatestHistory(Long standardId) {
        return nameExecHistoryMapper.getLatestHistory(standardId);
    }

    @Override
    public List<NameExecHistoryDTO> getHistoryList(NameExecHistoryDTO nameExecHistoryDTO) {
        return nameExecHistoryMapper.getHistoryList(nameExecHistoryDTO);
    }

    @Override
    public NameExecHistoryDTO detail(Long historyId) {
        return nameExecHistoryMapper.detail(historyId);
    }

    @Override
    public List<NameStandardHisReportVO> getReport(Long standardId) {
        return nameExecHistoryMapper.getReport(standardId);
    }
}
