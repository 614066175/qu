package com.hand.hdsp.quality.infra.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>批数据方案结果表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface BatchResultMapper extends BaseMapper<BatchResult> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    List<BatchResultDTO> listByGroup(BatchResultDTO batchResultDTO);

    /**
     * 查看评估方案的评估结果头
     *
     * @param batchResultDTO
     * @return
     */
    BatchResultDTO listResultHead(BatchResultDTO batchResultDTO);

    /**
     * 根据分组查询对应的评估方案执行记录
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    List<BatchResultDTO> listHistory(BatchResultDTO batchResultDTO);

    /**
     * 质量分析汇总:数据质量分数，规则总数，异常规则数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    Map<String,Object> listResultMap(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate);


    /**
     * 查询时间范围内各个校验类型成功总数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return CheckTypePercentageVO
     */
    List<CheckTypePercentageVO> listSUCCESSTypeCount(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate);

    /**
     * 询时间范围内各个校验类型总数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    List<CheckTypePercentageVO> listAllTypeCount(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate);

    /**
     * 主要可改进指标:规则总数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @param rule
     * @return
     */
    List<RuleVO> listRule(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("rule") String rule);

    /**
     * 主要可改进指标:异常规则数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @param rule
     * @return
     */
    List<RuleVO> listErrorRule(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("rule") String rule);

    /**
     * 每日分数走势
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    List<MarkTrendVO> listMarkTrend(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate);

    /**
     * 展示不同类型异常数
     *
     * @return
     */
    List<RuleExceptionVO> listRuleException(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate);

    /**
     * 展示不同等级每日告警数
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     */
    List<WarningLevelVO> listWarningLevel(@Param("tenantId") Long tenantId,@Param("startDate") String startDate,@Param("endDate") String endDate);
}
