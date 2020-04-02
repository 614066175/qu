package com.hand.hdsp.quality.infra.mapper;

import java.util.List;
import java.util.Map;

import com.hand.hdsp.quality.api.dto.StreamingResultDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import com.hand.hdsp.quality.infra.vo.MarkTrendVO;
import com.hand.hdsp.quality.infra.vo.RuleExceptionVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>实时数据方案结果表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultMapper extends BaseMapper<StreamingResult> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param streamingResultDTO
     * @return StreamingResultDTO
     */
    List<StreamingResultDTO> listByGroup(StreamingResultDTO streamingResultDTO);

    /**
     * 查看评估方案的评估结果头
     *
     * @param streamingResultDTO
     * @return StreamingResultDTO
     */
    StreamingResultDTO showResultHead(StreamingResultDTO streamingResultDTO);

    /**
     *  根据分组查询对应的评估方案执行记录
     *
     * @param streamingResultDTO
     * @return StreamingResultDTO
     */
    List<StreamingResultDTO> listHistory(StreamingResultDTO streamingResultDTO);

    /**
     * 质量分析汇总:数据质量分数，规则总数，异常规则数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    Map<String,Object> listResultMap(@Param("tenantId") Long tenantId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 每日分数走势
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    List<MarkTrendVO> listMarkTrend(@Param("tenantId") Long tenantId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 展示不同等级每日告警数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    List<WarningLevelVO> listWarningLevel(@Param("tenantId") Long tenantId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询topic延迟
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @param topicInfo
     * @return
     */
    List<Map<String,Object>> listDelayTopic(@Param("tenantId") Long tenantId, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("topicInfo") String topicInfo);

    /**
     * 展示不同类型异常数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    List<RuleExceptionVO> listRuleError(@Param("tenantId") Long tenantId, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
