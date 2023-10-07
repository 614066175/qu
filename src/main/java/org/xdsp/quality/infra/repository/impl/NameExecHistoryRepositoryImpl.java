package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.NameExecHistoryDTO;
import org.xdsp.quality.domain.entity.NameExecHistory;
import org.xdsp.quality.domain.repository.NameExecHistoryRepository;
import org.xdsp.quality.infra.mapper.NameExecHistoryMapper;
import org.xdsp.quality.infra.vo.NameStandardHisReportVO;

import java.util.List;

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
