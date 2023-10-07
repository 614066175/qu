package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.NameExecHistoryDTO;
import org.xdsp.quality.domain.entity.NameExecHistory;
import org.xdsp.quality.infra.vo.NameStandardHisReportVO;

import java.util.List;

/**
 * <p>命名标准执行历史表Mapper</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameExecHistoryMapper extends BaseMapper<NameExecHistory> {

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
     * @param nameExecHistoryDTO 标准ID
     * @return List<NameExecHistoryDTO>
     */
    List<NameExecHistoryDTO> getHistoryList(NameExecHistoryDTO nameExecHistoryDTO);

    /**
     * 执行历史明细
     *
     * @param historyId 执行历史ID
     * @return NameExecHistoryDTO
     */
    NameExecHistoryDTO detail(Long historyId);

    /**
     * 获取校验报表
     *
     * @param standardId 标准ID
     * @return List<NameStandardHisReportVO>
     */
    List<NameStandardHisReportVO> getReport(Long standardId);
}
