package com.hand.hdsp.quality.app.service.impl;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchResultService;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.feign.ExecutionFlowFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.util.TimeToString;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hzero.boot.platform.profile.ProfileClient;
import org.hzero.core.util.ResponseUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.ExceptionParam.*;

/**
 * <p>批数据方案结果表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
@Slf4j
public class BatchResultServiceImpl implements BatchResultService {

    private final ExecutionFlowFeign executionFlowFeign;
    private final BatchResultMapper batchResultMapper;
    private final BatchResultItemMapper batchResultItemMapper;
    private final BatchResultBaseRepository batchResultBaseRepository;
    private final BatchResultRepository batchResultRepository;
    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final BatchPlanRepository batchPlanRepository;
    private final String LEFT_Braces = "{";
    private final String INCREMENTSTRATEGY = "NONE";

    @Autowired
    private StringRedisTemplate redisTemplate;
    private final MongoTemplate mongoTemplate;
    private final ProfileClient profileClient;

    public static final String DOWN_EXCEPTION_NUM = "XQUA.DOWN_EXCEPTION_NUM";
    public static final String DOWN_EXCEPTION_BATCH_SIZE = "XQUA.DOWN_EXCEPTION_BATCH_SIZE";

    public BatchResultServiceImpl(ExecutionFlowFeign executionFlowFeign,
                                  BatchResultMapper batchResultMapper,
                                  BatchResultItemMapper batchResultItemMapper,
                                  BatchResultBaseRepository batchResultBaseRepository, BatchResultRepository batchResultRepository, BatchPlanBaseRepository batchPlanBaseRepository, BatchPlanRepository batchPlanRepository, MongoTemplate mongoTemplate, ProfileClient profileClient) {
        this.executionFlowFeign = executionFlowFeign;
        this.batchResultMapper = batchResultMapper;
        this.batchResultItemMapper = batchResultItemMapper;
        this.batchResultBaseRepository = batchResultBaseRepository;
        this.batchResultRepository = batchResultRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanRepository = batchPlanRepository;
        this.mongoTemplate = mongoTemplate;
        this.profileClient = profileClient;
    }


    @Override
    public ResultObjDTO showLog(Long tenantId, int execId, String jobId) {
        ResponseEntity<String> result = executionFlowFeign.getJobLog(tenantId, execId, jobId);
        if (ResponseUtils.isFailed(result)) {
            // 获取异常信息
            ExceptionResponse response = ResponseUtils.getResponse(result, ExceptionResponse.class);
            throw new CommonException(response.getMessage());
        }
        return ResponseUtils.getResponse(result, ResultObjDTO.class);
    }

    @Override
    public BatchResultDTO listResultDetail(BatchResultDTO batchResultDTO) {
        List<BatchResultMarkDTO> batchResultMarkDTOS = batchResultMapper.listResultDetail(batchResultDTO);
        batchResultDTO.setBatchResultMarkDTOList(batchResultMarkDTOS.stream()
                .filter(Objects::nonNull).collect(Collectors.toList()));
        List<BatchResultBaseDTO> batchResultBaseDTOList = batchResultBaseRepository.selectDTOByCondition(
                Condition.builder(BatchResultBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchResultBase.FIELD_RESULT_ID, batchResultDTO.getResultId())
                                .andEqualTo(BatchResultBase.FIELD_TENANT_ID, batchResultDTO.getTenantId()))
                        .build());

        //将所有告警等级Json转换合并成集合
        List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
        List<ResultWaringVO> resultWaringVOList = new ArrayList<>();
        for (BatchResultBaseDTO dto : batchResultBaseDTOList) {
            //获取base下所有校验项的告警等级Json
            List<String> warningLevelJsonList = batchResultItemMapper.selectWaringLevelJson(dto);
            //将所有告警等级Json转换合并成集合
            warningLevelJsonList.forEach(warningLevelJson -> warningLevelVOList.addAll(JsonUtils.json2WarningLevelVO(warningLevelJson)));

            //数据标准告警JSON集合
            List<String> standardJsonList = batchResultItemMapper.dataStandardWaringLevelVO(dto);
            if (CollectionUtils.isNotEmpty(standardJsonList)) {
                batchResultDTO.setDataStandardCount(standardJsonList.size() + Optional.ofNullable(batchResultDTO.getDataStandardCount()).orElse(0L));
                standardJsonList.forEach(standardWarningLevel -> {
                    List<WarningLevelVO> warningLevelVOS = JsonUtils.json2WarningLevelVO(standardWarningLevel);
                    if (CollectionUtils.isNotEmpty(warningLevelVOS)) {
                        batchResultDTO.setExceptionDataStandardCount(Optional.ofNullable(batchResultDTO.getExceptionDataStandardCount()).orElse(0L) + 1L);
                    }
                });
            }
            batchResultDTO.setRuleCount(dto.getRuleCount() +
                    Optional.ofNullable(batchResultDTO.getRuleCount()).orElse(0L));
            batchResultDTO.setExceptionRuleCount(dto.getExceptionRuleCount() +
                    Optional.ofNullable(batchResultDTO.getExceptionRuleCount()).orElse(0L));
            batchResultDTO.setCheckItemCount(dto.getCheckItemCount() +
                    Optional.ofNullable(batchResultDTO.getCheckItemCount()).orElse(0L));
            batchResultDTO.setExceptionCheckItemCount(dto.getExceptionCheckItemCount() +
                    Optional.ofNullable(batchResultDTO.getExceptionCheckItemCount()).orElse(0L));
        }
        //合并处理每个检验项的告警等级
        Map<String, Long> collect = warningLevelVOList.stream()
                .collect(
                        Collectors.toMap(WarningLevelVO::getWarningLevel,
                                WarningLevelVO::getLevelCount,
                                Long::sum));
        //返回方案下的所有告警等级以及对应的数量
        collect.forEach((k, v) -> {
            ResultWaringVO resultWaringVO = ResultWaringVO.builder()
                    .warningLevel(k)
                    .countSum(v)
                    .build();
            resultWaringVOList.add(resultWaringVO);
        });
        batchResultDTO.setResultWaringVOList(resultWaringVOList);
        return batchResultDTO;
    }

    @Override
    public Page<BatchResultBaseDTO> listExceptionDetailHead(Long resultId, Long tenantId, PageRequest pageRequest) {
        //根据resultId查询方案Id
        BatchResultDTO dto = batchResultRepository.selectDTOByPrimaryKey(resultId);
        //查询方案下所有resultBase
        if (Objects.isNull(dto)) {
            throw new CommonException(ErrorCode.BATCH_RESULT_NOT_EXIST);
        }
        Page<BatchResultBaseDTO> page = batchResultBaseRepository.pageAndSortDTO(pageRequest, BatchResultBaseDTO.builder().resultId(resultId).tenantId(tenantId).build());
        List<BatchResultBaseDTO> batchResultBaseDTOList = page.getContent();
        if (CollectionUtils.isNotEmpty(batchResultBaseDTOList)) {
            for (BatchResultBaseDTO batchResultBaseDTO : batchResultBaseDTOList) {
                //获取base下所有校验项的告警等级Json
                List<String> warningLevelJsonList = batchResultItemMapper.selectWaringLevelJson(batchResultBaseDTO);
                //将所有告警等级Json转换合并成集合
                List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
                warningLevelJsonList.forEach(warningLevelJson -> warningLevelVOList.addAll(JsonUtils.json2WarningLevelVO(warningLevelJson)));
                //合并处理每个检验项的告警等级
                Map<String, Long> collect = warningLevelVOList.stream()
                        .collect(
                                Collectors.toMap(WarningLevelVO::getWarningLevel,
                                        WarningLevelVO::getLevelCount,
                                        Long::sum));
                //定义告警结果对象集合
                List<ResultWaringVO> resultWaringVOList = new ArrayList<>();
                collect.forEach((k, v) -> {
                    ResultWaringVO resultWaringVO = ResultWaringVO.builder()
                            .warningLevel(k)
                            .countSum(v)
                            .build();
                    resultWaringVOList.add(resultWaringVO);
                });
                //设置到每个base中
                batchResultBaseDTO.setResultWaringVOList(resultWaringVOList);
                if (Objects.isNull(batchResultBaseDTO.getDataCount())) {
                    batchResultBaseDTO.setDataCount(0L);
                }
                page.setContent(batchResultBaseDTOList);
            }
        }
        return page;
    }

    @Override
    @SuppressWarnings(value = "all")
    public Page<Map> listExceptionDetail(ExceptionDataDTO exceptionDataDTO, PageRequest pageRequest) {
        if (Objects.isNull(exceptionDataDTO.getPlanBaseId())) {
            throw new CommonException(ErrorCode.PLAN_BASE_ID_IS_EMPTY);
        }

        Long resultBaseId = batchResultBaseRepository.selectMaxResultBaseId(exceptionDataDTO.getPlanBaseId());
        if (Objects.isNull(resultBaseId)) {
            throw new CommonException(ErrorCode.BATCH_RESULT_NOT_EXIST);
        }
        //通过mongo来进行查询
        List<Criteria> criteriaList=new ArrayList<>();
        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(exceptionDataDTO.getRuleName())) {
            criteriaList.add(Criteria.where(RULE_NAME).regex(String.format("^.*%s.*$", exceptionDataDTO.getRuleName())));
        }
        if (StringUtils.isNotEmpty(exceptionDataDTO.getWarningLevel())) {
            //告警等级
            criteriaList.add(Criteria.where(WARNING_LEVEL).is(exceptionDataDTO.getWarningLevel()));
        }
        if(CollectionUtils.isNotEmpty(criteriaList)){
            Criteria[] criteriaArrary=new Criteria[criteriaList.size()];
            criteria.andOperator(criteriaList.toArray(criteriaArrary));
        }
        String collectionName = String.format("%d_%d", exceptionDataDTO.getPlanBaseId(), resultBaseId);
        //查询总数
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.count().as("count"),
                Aggregation.project("count")).withOptions(AggregationOptions.builder().allowDiskUse(true).build());
        AggregationResults<Map> aggregate = mongoTemplate.aggregate(agg, collectionName, Map.class);
        Long total;
        if (CollectionUtils.isEmpty(aggregate.getMappedResults())) {
            total = 0L;
        } else {
            List<Map> mappedResults = aggregate.getMappedResults();
            total = Long.parseLong(mappedResults.get(0).get("count").toString());
        }
        if (total == 0) {
            return new Page<>();
        }
        Query query = new Query();
        //排除_id字段
        query.fields().exclude("_id");
        query.addCriteria(criteria);
        //设置分页
        query.with(org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize()));
        List<Map> content = mongoTemplate.find(query, Map.class, collectionName);
        return new Page<>(content, new PageInfo(pageRequest.getPage(), pageRequest.getSize()), total);
    }

    @SuppressWarnings(value = "all")
    public List<Map> getContent(Query query, String collectionName, int pageNum, int pageSize) {
        // 通过 _id 来排序
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        if (pageNum != 0) {
            // number 参数是为了查上一页的最后一条数据
            int number = pageNum * pageSize;
            query.limit(number);
            List<Map> content = mongoTemplate.find(query, Map.class, collectionName);
            if (CollectionUtils.isEmpty(content)) {
                return null;
            }
            // 取出最后一条数据
            Map<String, Object> data = content.get(content.size() - 1);
            // 取到上一页的最后一条数据 id，当作条件查接下来的数据
            //mongo默认使用的时ObjectId，实际上就是以插入数据库的时间进行排序，所以直接比较ObjectId即可
            ObjectId id = (ObjectId) data.get("_id");
            // 从上一页最后一条开始查（大于不包括这一条）使用gt
            query.addCriteria(Criteria.where("_id").gt(id));
        }
        // 页大小重新赋值，覆盖 number 参数
        query.limit(pageSize);
        //去掉_id字段的返回
        query.fields().exclude("_id");
        // 即可得到第n页数据
        return mongoTemplate.find(query, Map.class, collectionName);
    }

    @Override
    public List<TimeRangeDTO> listProblemData(TimeRangeDTO timeRangeDTO) {
        TimeToString.timeToString(timeRangeDTO);
        BatchPlanBase batchPlanBase = batchPlanBaseRepository.selectByPrimaryKey(timeRangeDTO.getPlanBaseId());
        if (batchPlanBase == null) {
            throw new CommonException(ErrorCode.BATCH_PLAN_BASE_NOT_EXIST);
        }
        List<TimeRangeDTO> list;
        if (INCREMENTSTRATEGY.equals(batchPlanBase.getIncrementStrategy())) {
            //全量同步时，问题数据数量趋势
            list = batchResultMapper.selectProblemTrend(timeRangeDTO);
        } else {
            //增量同步时，问题数据数量趋势
            list = batchResultMapper.selectProblemWithIncre(timeRangeDTO);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            //筛选出来符合规则的异常统计，然后根据日期进行分组再次求和
            Map<String, LongSummaryStatistics> collect = list.stream().filter(u -> u.getWarningLevel().contains(LEFT_Braces))
                    .map(u -> TimeRangeDTO.builder().problemDataCount(JsonUtils.json2WarningLevelVO(u.getWarningLevel())
                                    .stream().collect(Collectors.summarizingLong(WarningLevelVO::getLevelCount)).getSum())
                            .dateGroup(u.getDateGroup())
                            .build())
                    .collect(Collectors.groupingBy(TimeRangeDTO::getDateGroup, Collectors.summarizingLong(TimeRangeDTO::getProblemDataCount)));
            //将分组求和的数据转成List
            list = collect.entrySet().stream().map(c -> TimeRangeDTO.builder()
                            .dateGroup(c.getKey())
                            .problemDataCount(c.getValue().getSum()).build())
                    .collect(Collectors.toList());
            //对list按照时间正序排序
            list = list.stream().sorted(Comparator.comparing(TimeRangeDTO::getDateGroup)).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    @SuppressWarnings(value = "all")
    public void exceptionDataDownload(ExceptionDataDTO exceptionDataDTO, HttpServletRequest request, HttpServletResponse response) {
        // 查找异常数据
        Long resultBaseId = batchResultBaseRepository.selectMaxResultBaseId(exceptionDataDTO.getPlanBaseId());
        if (Objects.isNull(resultBaseId)) {
            throw new CommonException(ErrorCode.BATCH_RESULT_NOT_EXIST);
        }
        String collectionName = String.format("%d_%d", exceptionDataDTO.getPlanBaseId(), resultBaseId);
        //获取总数，分批下载
        Aggregation agg = Aggregation.newAggregation(Aggregation.count().as("count"),
                Aggregation.project("count")).withOptions(AggregationOptions.builder().allowDiskUse(true).build());
        AggregationResults<Map> aggregate = mongoTemplate.aggregate(agg, collectionName, Map.class);
        Long total;
        if (CollectionUtils.isEmpty(aggregate.getMappedResults())) {
            total = 0L;
        } else {
            List<Map> mappedResults = aggregate.getMappedResults();
            total = Long.parseLong(mappedResults.get(0).get("count").toString());
        }


        // 写入到excel
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            request.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            response.setHeader("Content-disposition",
                    String.format("attachment;filename=%s.xlsx", "Exception-Data", "UTF-8"));

            ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX, true);
            int batchSize = Integer.parseInt(Optional.ofNullable(profileClient.getProfileValueByOptions(exceptionDataDTO.getTenantId(), null, null, DOWN_EXCEPTION_BATCH_SIZE)).orElse("10000"));
            //多少页，也就是分多少批
            long pageNumber = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
            int totalAmount = 0;
            int sheetNo = 1;
            Sheet sheet = new Sheet(sheetNo, 0);
            List<List<String>> headList = null;
            for (int i = 0; i < pageNumber; i++) {
                //一百万分sheet页
                if (totalAmount >= 1000000) {
                    sheet = new Sheet(++sheetNo, 0);
                    sheet.setSheetName("Exception-Data" + "_" + sheetNo);
                    if (headList != null) {
                        sheet.setHead(headList);
                    }
                }

                //通过mongo来进行查询
                Query query = new Query();

                // 通过 _id 来排序
                query.with(Sort.by(Sort.Direction.ASC, "_id"));
                //去掉_id和#PK等返回
                query.fields().exclude("_id", "#pk", "#planBaseId", "#resultBaseId");
                //分页查询
                query.with(org.springframework.data.domain.PageRequest.of(i, batchSize));

                List<Map> maps = mongoTemplate.find(query, Map.class, collectionName);
                List<List<Object>> dataList = maps.stream().map(map -> {
                    List<Object> list = new ArrayList();
                    Set<String> set = map.keySet();
                    set.forEach(s -> list.add(map.get(s)));
                    return list;
                }).collect(Collectors.toList());

                Set<String> set = maps.get(0).keySet();
                if (headList == null) {
                    headList = set.stream().map(key -> {
                        List<String> list = new ArrayList<>();
                        switch (key){
                            case RULE_NAME:
                                list.add("规则名称");
                                break;
                            case EXCEPTION_INFO:
                                list.add("异常信息");
                                break;
                            case WARNING_LEVEL:
                                list.add("告警等级");
                                break;
                            default:
                                list.add(key);
                        }
                        return list;
                    }).collect(Collectors.toList());
                    sheet.setHead(headList);
                }
                writer.write1(dataList, sheet);
                outputStream.flush();
                totalAmount += maps.size();
            }
            writer.finish();
        } catch (IOException e) {
            throw new CommonException(ErrorCode.EXCEL_WRITE_ERROR, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resultPlanNameFix() {
        List<BatchResult> batchResults = batchResultRepository.selectByCondition(Condition.builder(BatchResult.class)
                .andWhere(Sqls.custom().andIsNull(BatchResult.FIELD_PLAN_NAME))
                .build());
        if (CollectionUtils.isNotEmpty(batchResults)) {
            List<BatchResult> updateBatchResults = new ArrayList<>();
            List<Long> deleteResultIds = new ArrayList<>();
            for (BatchResult batchResult : batchResults) {
                BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(batchResult.getPlanId());
                if (batchPlan == null) {
                    deleteResultIds.add(batchResult.getResultId());
//                    batchResultRepository.deleteByPrimaryKey(batchResult);
//                    List<BatchResultBase> resultBases = batchResultBaseRepository.select(BatchResultBase.builder().resultId(batchResult.getResultId()).build());
//                    if(CollectionUtils.isNotEmpty(resultBases)){
//                        for (BatchResultBase resultBase : resultBases) {
//                            List<BatchResultRule> batchResultRules = batchResultRuleRepository.select(BatchResultRule.builder().resultBaseId(resultBase.getResultBaseId()).build());
//                            if(CollectionUtils.isNotEmpty(batchResultRules)){
//                                for (BatchResultRule batchResultRule : batchResultRules) {
//                                    List<BatchResultItem> resultItems = batchResultItemRepository.select(BatchResultItem.builder().resultRuleId(batchResultRule.getResultRuleId()).build());
//                                    batchResultItemRepository.batchDeleteByPrimaryKey(resultItems);
//                                }
//                                batchResultRuleRepository.batchDeleteByPrimaryKey(batchResultRules);
//                            }
//                        }
//                        batchResultBaseRepository.batchDeleteByPrimaryKey(resultBases);
//                    }
                    continue;
                }
                //数据修复
                batchResult.setPlanName(batchPlan.getPlanName());
                updateBatchResults.add(batchResult);
            }
            if (CollectionUtils.isNotEmpty(updateBatchResults)) {
                batchResultRepository.batchUpdateOptional(updateBatchResults, BatchResult.FIELD_PLAN_NAME);
            }
            if (CollectionUtils.isNotEmpty(deleteResultIds)) {
                log.info("进行删除");
                batchResultMapper.deleteResultItem(deleteResultIds);
                batchResultMapper.deleteResultRule(deleteResultIds);
                batchResultMapper.deleteResultBase(deleteResultIds);
                batchResultMapper.deleteResult(deleteResultIds);
            }
        }
    }

}
