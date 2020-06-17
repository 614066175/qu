package com.hand.hdsp.quality.infra.measure.measure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.api.dto.RelationshipDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.exception.MessageException;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>表间关系</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.RuleType.TABLE_RELATION)
public class RelTableMeasure implements Measure {

    private final DatasourceFeign datasourceFeign;
    private final ItemTemplateSqlRepository templateSqlRepository;

    public RelTableMeasure(DatasourceFeign datasourceFeign, ItemTemplateSqlRepository templateSqlRepository) {
        this.datasourceFeign = datasourceFeign;
        this.templateSqlRepository = templateSqlRepository;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchPlanRelTable batchPlanRelTable = param.getBatchPlanRelTable();
        DatasourceDTO datasourceDTO = param.getDatasourceDTO();
        List<RelationshipDTO> relationshipDTOList = JsonUtils.json2Relationship(batchPlanRelTable.getRelationship());
        List<WarningLevelDTO> warningLevelList = JsonUtils.json2WarningLevel(batchPlanRelTable.getWarningLevel());
        BatchResultItem batchResultItem = param.getBatchResultItem();


        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) count from ")
                .append(batchResultBase.getObjectName())
                .append(" source, ");
        if (StringUtils.isBlank(batchPlanRelTable.getWhereCondition())) {
            sql.append(batchPlanRelTable.getRelSchema())
                    .append(".")
                    .append(batchPlanRelTable.getRelTableName())
                    .append(" rel ");
        } else {
            sql.append("( select * from ")
                    .append(batchPlanRelTable.getRelSchema())
                    .append(".")
                    .append(batchPlanRelTable.getRelTableName())
                    .append(" where ")
                    .append(batchPlanRelTable.getWhereCondition())
                    .append(" ) rel ");
        }
        if (!relationshipDTOList.isEmpty()) {
            sql.append(" where 1 = 1 ");
            for (RelationshipDTO relationshipDTO : relationshipDTOList) {
                sql.append(" and source.").append(relationshipDTO.getSourceFieldName())
                        .append(relationshipDTO.getRelCode())
                        .append(" rel.").append(relationshipDTO.getRelFieldName());
            }
        }

        datasourceDTO.setSql(sql.toString());
        List<Map<String, Long>> result = ResponseUtils.getResponse(datasourceFeign.execSql(datasourceDTO.getTenantId(), datasourceDTO), new TypeReference<List<Map<String, Long>>>() {
        });

        //获取表行数
        Long dataCount = batchResultBase.getDataCount();
        if (dataCount == null) {
            List<ItemTemplateSql> list = templateSqlRepository.select(ItemTemplateSql.builder()
                    .checkItem(PlanConstant.CheckItem.TABLE_LINE)
                    .datasourceType(param.getDatasourceType())
                    .build());

            Map<String, String> variables = new HashMap<>();
            variables.put("table", batchResultBase.getObjectName());

            datasourceDTO.setSql(MeasureUtil.replaceVariable(list.get(0).getSqlContent(), variables, batchResultBase.getWhereCondition()));
            List<HashMap<String, String>> response = ResponseUtils.getResponse(datasourceFeign.execSql(tenantId, datasourceDTO), new TypeReference<List<HashMap<String, String>>>() {
            });
            if (response.size() != 1 || response.get(0).size() != 1) {
                throw new MessageException(MessageAccessor.getMessage(ErrorCode.CUSTOM_SQL_ONE_VALUE).getDesc());
            }

            dataCount = (Long) response.get(0).values().toArray()[0];
            batchResultBase.setDataCount(dataCount);
        }

        //计算准确率
        BigDecimal a = new BigDecimal(result.get(0).get("count"));
        BigDecimal b = BigDecimal.valueOf(dataCount);
        BigDecimal rate = a.divide(b, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        batchResultItem.setActualValue(rate.toString() + BaseConstants.Symbol.PERCENTAGE);
        batchResultItem.setRelTableName(batchPlanRelTable.getRelTableName());

        for (WarningLevelDTO warningLevelDTO : warningLevelList) {
            if (warningLevelDTO.getStartValue().compareTo(rate) <= 0
                    && warningLevelDTO.getEndValue().compareTo(rate) >= 0) {
                batchResultItem.setWarningLevel(warningLevelDTO.getWarningLevel());
                batchResultItem.setExceptionInfo("准确率（校验表与目标表匹配条数/校验表总条数）超出阈值范围");
            }
        }

        return batchResultItem;
    }
}