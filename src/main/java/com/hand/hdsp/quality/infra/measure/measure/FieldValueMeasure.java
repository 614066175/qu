package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.core.util.DiscoveryHelper;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureResultDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.util.PlanExceptionUtil;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareSymbol.EQUAL;
import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareWay.RANGE;
import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareWay.VALUE;

/**
 * <p>
 * 字段值
 * 特殊处理值集校验
 * 其他校验类型继续走通用SQL
 * </p>
 *
 * @author feng.liu01@hand-china.com 2020-06-09 10:06:43
 */
@CheckItem("FIELD_VALUE")
public class FieldValueMeasure implements Measure {

    private static final String COUNT = "COUNT";
    private static final String EQUAL_SQL = " and ${field} = (%s)";
    private static final String NOT_EQUAL_SQL = " and ${field} != (%s)";
    private static final String START_SQL = " and ${field} > %s";
    private static final String END_SQL = " and ${field} < %s";
    private final ItemTemplateSqlRepository templateSqlRepository;
    private final LovAdapter lovAdapter;
    private final DriverSessionService driverSessionService;

    @Autowired
    private DiscoveryHelper discoveryHelper;

    @Autowired
    private RestTemplate restTemplate;


    public FieldValueMeasure(ItemTemplateSqlRepository templateSqlRepository,
                             LovAdapter lovAdapter, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.lovAdapter = lovAdapter;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        List<WarningLevelDTO> warningLevelList = param.getWarningLevelList();
        if (CollectionUtils.isEmpty(param.getExceptionMapList())) {
            param.setExceptionMapList(new ArrayList<>());
        }
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());

        // 值集校验
        String countType = param.getCountType();
        if (PlanConstant.CountType.LOV_VALUE.equals(countType)) {
            WarningLevelDTO warningLevelDTO = warningLevelList.get(0);
            LovDTO lovDTO = lovAdapter.queryLovInfo(warningLevelDTO.getLovCode(), tenantId);
            String lovValueString = Strings.EMPTY;
            if ("URL".equals(lovDTO.getLovTypeCode())) {
                //设置size为0查询所有
                //尝试feign调用platform接口报错 400 bad request
//                ResponseEntity<String> stringResponseEntity = platformFeign.queryLovData(warningLevelDTO.getLovCode(), tenantId, null, null, 0, tenantId);
//                List<Map<String, Object>> body = ResponseUtils.getResponse(stringResponseEntity, new TypeReference<List<Map<String, Object>>>() {
//                });
//                if (CollectionUtils.isEmpty(body)) {
//                    throw new CommonException(ErrorCode.NOT_FIND_VALUE);
//                }
                //设置size为0查询所有
                Map<String, String> params = new HashMap<>();
                params.put("organizationId", String.valueOf(tenantId));
                params.put("size", String.valueOf(0));

                String serverCode = lovDTO.getRouteName();
                String json = restTemplate.getForObject("http://" + discoveryHelper.getServerName(serverCode) + preProcessUrlParam(lovDTO.getCustomUrl(), params), String.class, params);
                List<Map<String, Object>> body = JsonUtil.toObj(json, new TypeReference<List<Map<String, Object>>>() {
                });


                if (CollectionUtils.isNotEmpty(body)) {
                    Map<String, Object> result = body.get(0);
                    //判断是不是分页结果
                    if (result.containsKey("totalPages")) {
                        //如果是，那么结果取content
                        body = (List<Map<String, Object>>) result.get("content");
                    }
                }
                //查询值集视图 一般值集编码和视图编码保持一致，不一致查表
                LovViewDTO lovViewDTO = lovAdapter.queryLovViewInfo(warningLevelDTO.getLovCode(), tenantId);

                //找到value_field
                List<String> values = body.stream().map(map -> String.valueOf(map.get(lovViewDTO.getValueField()))).collect(Collectors.toList());
                lovValueString = values.stream()
                        .map(value -> "'" + value + "'")
                        .collect(Collectors.joining(BaseConstants.Symbol.COMMA));
            }
            if ("IDP".equals(lovDTO.getLovTypeCode())) {
                List<LovValueDTO> lovValueDTOList = lovAdapter.queryLovValue(warningLevelDTO.getLovCode(), tenantId);
                if (CollectionUtils.isEmpty(lovValueDTOList)) {
                    throw new CommonException(ErrorCode.NOT_FIND_VALUE);
                }
                lovValueString = lovValueDTOList.stream()
                        .map(lovValueDTO -> "'" + lovValueDTO.getValue() + "'")
                        .collect(Collectors.joining(BaseConstants.Symbol.COMMA));
            }
            if (Strings.isEmpty(lovValueString)) {
                throw new CommonException(ErrorCode.NOT_FIND_VALUE);
            }

            // 查询要执行的SQL
            ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                    .checkItem(countType + "_" + warningLevelDTO.getCompareSymbol())
                    .datasourceType(batchResultBase.getDatasourceType())
                    .build());

            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("listValue", lovValueString);
            String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
            List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(), sql);
            if (CollectionUtils.isNotEmpty(response)) {
                batchResultItem.setWarningLevel(JsonUtils.object2Json(Collections.singletonList(WarningLevelVO.builder()
                        .warningLevel(warningLevelDTO.getWarningLevel())
                        .build())));
                PlanExceptionUtil.getPlanException(param, batchResultBase, sql, driverSession, warningLevelDTO);
                batchResultItem.setExceptionInfo("存在字段值满足值集校验配置");
            }

        } else if (PlanConstant.CountType.FIXED_VALUE.equals(countType)) {
            // 查询要执行的SQL
            ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                    .checkItem(param.getCheckItem())
                    .datasourceType(batchResultBase.getDatasourceType())
                    .build());

            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("size", PlanConstant.DEFAULT_SIZE + "");
            //用于统计告警规则，为后续聚合做准备
            List<WarningLevelVO> warningLevels = new ArrayList<>();
            //开始校验
            for (int i = 0; ; i++) {
                int start = i * PlanConstant.DEFAULT_SIZE;
                variables.put("start", start + "");
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
                List<MeasureResultDO> list = new ArrayList<>();
                maps.forEach((map) -> map.forEach((k, v) -> list.add(new MeasureResultDO(String.valueOf(v)))));
                for (MeasureResultDO measureResultDO : list) {
                    MeasureUtil.fixedCompare(param.getCompareWay(), measureResultDO.getResult(), warningLevelList, batchResultItem);
                    if (StringUtils.isNotBlank(batchResultItem.getWarningLevel())) {
                        //统计告警等级，为后续聚合做准备
                        warningLevels.addAll(JsonUtils.json2WarningLevelVO(batchResultItem.getWarningLevel()));
                        //设置对应行的实际值，这里需要再讨论
                        batchResultItem.setActualValue(measureResultDO.getResult());
                        //将告警等级设置为null，方便下条数据的循环
                        batchResultItem.setWarningLevel(null);
                    }
                }

                //第一页数据检验完成，将统计好的告警规则进行分组聚合
                Map<String, Long> collect = warningLevels.stream().collect(Collectors.groupingBy(WarningLevelVO::getWarningLevel, Collectors.counting()));
                batchResultItem.setWarningLevel(JsonUtils.object2Json(collect.entrySet()
                        .stream().map(map -> WarningLevelVO.builder()
                                .warningLevel(map.getKey())
                                .levelCount(map.getValue()).build()
                        ).collect(Collectors.toList())));

                //查询所有的异常数据
                warningLevelList.forEach(warn -> {
                    //固定值范围比较
                    StringBuilder condition = new StringBuilder();
                    if (RANGE.equals(param.getCompareWay())) {
                        if (Strings.isNotEmpty(warn.getStartValue())) {
                            condition.append(String.format(START_SQL, String.format("'%s'", warn.getStartValue())));
                        }
                        if (Strings.isNotEmpty(warn.getEndValue())) {
                            condition.append(String.format(END_SQL, String.format("'%s'", warn.getEndValue())));
                        }
                    }
                    //固定值比较
                    if (VALUE.equals(param.getCompareWay())) {
                        if (EQUAL.equals(warn.getCompareSymbol())) {
                            condition.append(String.format(EQUAL_SQL, String.format("'%s'", warn.getExpectedValue())));
                        } else {
                            condition.append(String.format(NOT_EQUAL_SQL, String.format("'%s'", warn.getExpectedValue())));
                        }
                    }
                    String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, String.format("%s%s", Optional.ofNullable(param.getWhereCondition()).orElse("1=1"), condition));
                    if (warn.getIfAlert() == 1L) {
                        PlanExceptionUtil.getPlanException(param, batchResultBase, sql, driverSession, warn);
                    }
                });
                //查询出来的数据量小于每页大小时（即已到最后一页了）退出
                if (list.size() < PlanConstant.DEFAULT_SIZE) {
                    break;
                }
            }

        }
        //枚举值
        else if (PlanConstant.CountType.ENUM_VALUE.equals(countType)) {
            List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
            warningLevelList.forEach(warningLevelDTO -> {
                String enumValue = warningLevelDTO.getEnumValue();
                List<String> enumValueList = new ArrayList<>();
                if (Strings.isNotEmpty(enumValue)) {
                    enumValueList = Arrays.asList(enumValue.split(","));
                }
                // 查询要执行的SQL
                ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                        .checkItem(countType + "_" + warningLevelDTO.getCompareSymbol())
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());

                Map<String, String> variables = new HashMap<>(8);
                variables.put("table", batchResultBase.getPackageObjectName());
                variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
                variables.put("listValue", enumValueList.stream()
                        .map(e -> "'" + e + "'")
                        .collect(Collectors.joining(BaseConstants.Symbol.COMMA))
                );
                String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(), sql);
                //oracle 只能是大写，hive只能是小写
                if (Integer.parseInt(response.get(0).values().toArray()[0].toString()) != 0) {
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount(Long.parseLong(String.valueOf(response.get(0).values().toArray()[0].toString())))
                                    .build());
                    PlanExceptionUtil.getPlanException(param, batchResultBase, sql, driverSession, warningLevelDTO);
                }
            });
            if (CollectionUtils.isNotEmpty(warningLevelVOList)) {
                AtomicLong count = new AtomicLong();
                warningLevelVOList.forEach(warningLevelVO -> count.set(count.get() + warningLevelVO.getLevelCount()));
                batchResultItem.setExceptionInfo(String.format("存在%d条数据满足告警要求", count.get()));
                batchResultItem.setWarningLevel(JsonUtil.toJson(warningLevelVOList));
            }
        }
        //逻辑值
        else if (PlanConstant.CountType.LOGIC_VALUE.equals(countType)) {
            List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
            warningLevelList.forEach(warningLevelDTO -> {
                // 查询要执行的SQL
                ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                        .checkItem(countType)
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());
                StringBuilder condition = new StringBuilder();
                //逻辑值范围比较
                if (RANGE.equals(param.getCompareWay())) {
                    if (Strings.isNotEmpty(warningLevelDTO.getStartValue())) {
                        condition.append(String.format(START_SQL, warningLevelDTO.getStartValue()));
                    }
                    if (Strings.isNotEmpty(warningLevelDTO.getEndValue())) {
                        condition.append(String.format(END_SQL, warningLevelDTO.getEndValue()));
                    }
                }
                //逻辑值值比较
                if (VALUE.equals(param.getCompareWay())) {
                    if (EQUAL.equals(warningLevelDTO.getCompareSymbol())) {
                        condition.append(String.format(EQUAL_SQL, warningLevelDTO.getExpectedValue()));
                    } else {
                        condition.append(String.format(NOT_EQUAL_SQL, warningLevelDTO.getExpectedValue()));
                    }

                }
                itemTemplateSql.setSqlContent(itemTemplateSql.getSqlContent() + condition);
                Map<String, String> variables = new HashMap<>(8);
                variables.put("table", batchResultBase.getPackageObjectName());
                variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
                String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(), sql);
                if (Integer.parseInt(response.get(0).values().toArray()[0].toString()) != 0) {
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount(Long.parseLong(response.get(0).values().toArray()[0].toString()))
                                    .build());
                    PlanExceptionUtil.getPlanException(param, batchResultBase, sql, driverSession, warningLevelDTO);
                }
            });
            if (CollectionUtils.isNotEmpty(warningLevelVOList)) {
                AtomicLong count = new AtomicLong();
                warningLevelVOList.forEach(warningLevelVO -> count.set(count.get() + warningLevelVO.getLevelCount()));
                batchResultItem.setExceptionInfo(String.format("存在%d条数据满足告警要求", count.get()));
                batchResultItem.setWarningLevel(JsonUtil.toJson(warningLevelVOList));
            }
        } else {
            throw new CommonException(ErrorCode.FIELD_NO_SUPPORT_CHECK_TYPE);
        }
        return batchResultItem;
    }

    private String preProcessUrlParam(String url, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder(url);
        Set<String> keySet = params.keySet();
        if (CollectionUtils.isNotEmpty(keySet)) {
            boolean firstKey = !url.contains("?");
            Iterator var6 = keySet.iterator();

            while (var6.hasNext()) {
                String key = (String) var6.next();
                if (firstKey) {
                    stringBuilder.append('?').append(key).append("={").append(key).append('}');
                    firstKey = false;
                } else {
                    stringBuilder.append('&').append(key).append("={").append(key).append('}');
                }
            }
        }

        return stringBuilder.toString();
    }
}
