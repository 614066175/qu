package com.hand.hdsp.quality.infra.measure.measure;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareWay.RANGE;
import static com.hand.hdsp.quality.infra.constant.PlanConstant.CompareWay.VALUE;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.hzero.starter.driver.core.session.DriverSession;

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
    private static final String VALUE_SQL = " and ${field} = (%s)";
    private static final String START_SQL = " and ${field} > %s";
    private static final String END_SQL=" and ${field} < %s";
    private final ItemTemplateSqlRepository templateSqlRepository;
    private final LovAdapter lovAdapter;
    private final DriverSessionService driverSessionService;

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
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());

        // 值集校验
        String countType = param.getCountType();
        if (PlanConstant.CountType.LOV_VALUE.equals(countType)) {
            WarningLevelDTO warningLevelDTO = warningLevelList.get(0);
            List<LovValueDTO> lovValueDTOList = lovAdapter.queryLovValue(warningLevelDTO.getLovCode(), tenantId);
            if (CollectionUtils.isEmpty(lovValueDTOList)) {
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
            variables.put("listValue", lovValueDTOList.stream()
                    .map(lovValueDTO -> "'" + lovValueDTO.getValue() + "'")
                    .collect(Collectors.joining(BaseConstants.Symbol.COMMA))
            );
            List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(),
                    MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
            if (CollectionUtils.isNotEmpty(response)) {
                batchResultItem.setWarningLevel(JsonUtils.object2Json(WarningLevelVO.builder()
                        .warningLevel(warningLevelDTO.getWarningLevel())
                        .build()));
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

            boolean successFlag = true;
            for (int i = 0; ; i++) {
                int start = i * PlanConstant.DEFAULT_SIZE;
                variables.put("start", start + "");
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
                List<MeasureResultDO> list = new ArrayList<>();
                maps.forEach((map) -> map.forEach((k, v) -> list.add(new MeasureResultDO(String.valueOf(v)))));
                for (MeasureResultDO measureResultDO : list) {
                    MeasureUtil.fixedCompare(param.getCompareWay(), measureResultDO.getResult(), warningLevelList, batchResultItem);
                    if (StringUtils.isNotBlank(batchResultItem.getWarningLevel())) {
                        batchResultItem.setActualValue(measureResultDO.getResult());
                        successFlag = false;
                        break;
                    }
                }

                //当成功标记为false 或者 查询出来的数据量小于每页大小时（即已到最后一页了）退出
                if (!successFlag || list.size() < PlanConstant.DEFAULT_SIZE) {
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
                if(Strings.isNotEmpty(enumValue)){
                    enumValueList= Arrays.asList(enumValue.split(","));
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
                        .map(e -> "'" +e+ "'")
                        .collect(Collectors.joining(BaseConstants.Symbol.COMMA))
                );
                List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(),
                        MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
                if(Integer.parseInt(String.valueOf(response.get(0).get(COUNT)))!=0){
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount((Long) response.get(0).get(COUNT))
                                    .build());
                }
            });
            AtomicLong count = new AtomicLong();
            warningLevelVOList.forEach(warningLevelVO -> count.set(count.get() + warningLevelVO.getLevelCount()));
            batchResultItem.setExceptionInfo(String.format("存在%d条数据满足告警要求", count.get()));
            batchResultItem.setWarningLevel(JsonUtil.toJson(warningLevelVOList));
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
                if(RANGE.equals(param.getCompareWay())){
                    if(Strings.isNotEmpty(warningLevelDTO.getStartValue())){
                        condition.append(String.format(START_SQL, warningLevelDTO.getExpectedValue()));
                    }
                    if(Strings.isNotEmpty(warningLevelDTO.getEndValue())){
                        condition.append(String.format(END_SQL, warningLevelDTO.getExpectedValue()));
                    }
                }
                //逻辑值值比较
                if(VALUE.equals(param.getCompareWay())){
                    condition.append(String.format(VALUE_SQL, warningLevelDTO.getExpectedValue()));
                }
                itemTemplateSql.setSqlContent(itemTemplateSql.getSqlContent() + condition);
                Map<String, String> variables = new HashMap<>(8);
                variables.put("table", batchResultBase.getPackageObjectName());
                variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
                List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(),
                        MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
                if(Integer.parseInt(String.valueOf(response.get(0).get(COUNT)))!=0){
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount((Long) response.get(0).get(COUNT))
                                    .build());
                }
            });
            AtomicLong count = new AtomicLong();
            warningLevelVOList.forEach(warningLevelVO -> count.set(count.get() + warningLevelVO.getLevelCount()));
            batchResultItem.setExceptionInfo(String.format("存在%d条数据满足告警要求", count.get()));
            batchResultItem.setWarningLevel(JsonUtil.toJson(warningLevelVOList));
        } else {
            throw new CommonException(ErrorCode.FIELD_NO_SUPPORT_CHECK_TYPE);
        }

        return batchResultItem;
    }
}
