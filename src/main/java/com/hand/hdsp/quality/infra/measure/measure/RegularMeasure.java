package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureResultDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.hzero.core.util.ResponseUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>正则表达式</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.CheckWay.REGULAR)
public class RegularMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;
    private final ItemTemplateSqlRepository templateSqlRepository;

    public RegularMeasure(DatasourceFeign datasourceFeign,
                          ItemTemplateSqlRepository templateSqlRepository) {
        this.datasourceFeign = datasourceFeign;
        this.templateSqlRepository = templateSqlRepository;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();

        // 查询要执行的SQL
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(PlanConstant.CheckWay.REGULAR)
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        // 用执行SQL的方式校验
        if (PlanConstant.TemplateSqlTag.SQL.equals(itemTemplateSql.getTag())) {
            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("regexp", param.getRegularExpression());

            datasourceDTO.setSql(MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));

            List<HashMap<String, String>> response = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<HashMap<String, String>>>() {
            });

            String value = response.get(0).values().toArray(new String[0])[0];
            BigDecimal result = new BigDecimal(value);
            if (result.compareTo(BigDecimal.ONE) != 0) {
                batchResultItem.setWarningLevel(param.getWarningLevelList().get(0).getWarningLevel());
                batchResultItem.setExceptionInfo("不满足正则表达式");
            }
        }
        // 查询出数据在Java里校验
        else if (PlanConstant.TemplateSqlTag.JAVA.equals(itemTemplateSql.getTag())) {
            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("size", PlanConstant.DEFAULT_SIZE + "");

            Pattern pattern = Pattern.compile(param.getRegularExpression());

            boolean successFlag = true;
            for (int i = 0; ; i++) {
                int start = i * PlanConstant.DEFAULT_SIZE;
                variables.put("start", start + "");

                datasourceDTO.setSql(MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));

                List<MeasureResultDO> list = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<MeasureResultDO>>() {
                });

                for (MeasureResultDO measureResultDO : list) {
                    if (!pattern.matcher(measureResultDO.getResult()).find()) {
                        batchResultItem.setActualValue(measureResultDO.getResult());
                        batchResultItem.setWarningLevel(param.getWarningLevelList().get(0).getWarningLevel());
                        batchResultItem.setExceptionInfo("不满足正则表达式");
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

        return batchResultItem;
    }
}
