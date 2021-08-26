package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.infra.vo.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * <p>批数据方案结果表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface BatchResultRepository extends BaseRepository<BatchResult, BatchResultDTO>, ProxySelf<BatchResultRepository> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param batchResultDTO
     * @param pageRequest
     * @return BatchResultDTO
     */
    Page<BatchResultDTO> listAll(BatchResultDTO batchResultDTO, PageRequest pageRequest);

    /**
     * 查看评估方案的评估结果头
     *
     * @param batchResultDTO
     * @return BatchResultDTO
     */
    BatchResultDTO showResultHead(BatchResultDTO batchResultDTO);

    /**
     * 根据分组查询对应的评估方案执行记录
     *
     * @param batchResultDTO
     * @param pageRequest
     * @return BatchResultDTO
     */
    Page<BatchResultDTO> listHistory(BatchResultDTO batchResultDTO, PageRequest pageRequest);

    /**
     * 查看质量分数，规则总数，异常规则数
     *
     * @param timeRangeDTO
     * @return
     */
    Map<String, Object> numberView(TimeRangeDTO timeRangeDTO);


    /**
     * 查看校验类型百分比
     *
     * @param timeRangeDTO
     * @return CheckTypePercentageVO
     */
    List<CheckTypePercentageVO> checkTypePercentage(TimeRangeDTO timeRangeDTO);

    /**
     * 灾区表占比情况
     *
     * @param timeRangeDTO
     * @return
     */
    List<ErrorTablePercentageVO> errorTablePercentage(TimeRangeDTO timeRangeDTO);

    /**
     * 问题触发次数统计
     * @param timeRangeDTO 时间条件
     * @return 返回值
     */
    List<ProblemTriggerVO> problemTrigger(TimeRangeDTO timeRangeDTO);

    /**
     * 问题知识库专用类型转换
     * @param type  类型字符串
     * @return 返回值
     */
    List<String> typeConvert(String type);

    /**
     * 灾区表-校验项占比情况
     *
     * @param timeRangeDTO
     * @return
     */
    List<ErrorTablePercentageVO> errorTableItemPercentage(TimeRangeDTO timeRangeDTO);

    /**
     * 数据质量分数走势
     *
     * @param timeRangeDTO
     * @return MarkTrendVO
     */
    List<MarkTrendVO> markTrend(TimeRangeDTO timeRangeDTO);

    /**
     * 表级，表间，字段级异常规则数
     *
     * @param timeRangeDTO
     * @return RuleExceptionVO
     */
    List<RuleExceptionVO> daysErrorRule(TimeRangeDTO timeRangeDTO);

    /**
     * 不同告警等级趋势
     *
     * @param timeRangeDTO
     * @return WarningLevelVO
     */
    List<WarningLevelVO> warningTrend(TimeRangeDTO timeRangeDTO);

    /**
     * 灾区表列表
     *
     * @param pageRequest
     * @param timeRangeDTO
     * @return
     */
    Page<ErrorTableListVO> errorTableList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO);

    /**
     * 灾区表-校验项列表
     *
     * @param pageRequest
     * @param timeRangeDTO
     * @return
     */
    Page<ErrorTableItemListVO> errorTableItemList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO);

    /**
     * 规则列表
     *
     * @param pageRequest
     * @param timeRangeDTO
     * @return
     */
    List<BatchPlanFieldDTO> ruleList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO);

    /**
     * 校验项列表
     *
     * @param pageRequest
     * @param timeRangeDTO
     * @return
     */
    List<BatchPlanFieldDTO> itemList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO);

    /**
     * 告警列表
     *
     * @param pageRequest
     * @param timeRangeDTO
     * @return
     */
    Page<BatchResultItemDTO> errorRuleList(PageRequest pageRequest, TimeRangeDTO timeRangeDTO);
}
