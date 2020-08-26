package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.driver.core.app.service.DriverSessionService;
import com.hand.hdsp.driver.core.app.service.session.DriverSession;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureResultDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

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
                throw new CommonException("未查询到值集的值");
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
                batchResultItem.setWarningLevel(warningLevelDTO.getWarningLevel());
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


        } else {
            throw new CommonException("字段值校验项不支持此校验类型");
        }

        return batchResultItem;
    }
}
