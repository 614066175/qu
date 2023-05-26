package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.SqlType.SQL;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2022/8/15 9:53
 * @since 1.0
 */
public class ActualValueUtil {
    //如果有多个实际值，默认只取3个,交给驱动执行分页
    public static final String ACTUAL_VALUE_SQL = "select %s.${field} %s";


    public static String getActualValue(MeasureParamDO param, BatchResultBase batchResultBase, String sql, DriverSession driverSession, Map<String, String> variables) {
        //获取实际值
        String tableName = batchResultBase.getPackageObjectName();
        //如果是sql类型，则
        if (SQL.equals(batchResultBase.getSqlType())) {
            //则使用定义的别名
            tableName = "sql_pack";
        }
        sql = String.format(ACTUAL_VALUE_SQL, tableName, sql.substring(sql.toLowerCase().indexOf("from")));
        //变量替换
        sql = MeasureUtil.replaceVariable(sql, variables, null);
        Page<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), sql, PageRequest.of(0, 3));
        if (maps != null && CollectionUtils.isNotEmpty(maps.getContent())) {
            List<Map<String, Object>> content = maps.getContent();
            List<String> values = content.stream().map(field -> String.valueOf(field.values().toArray()[0])).collect(Collectors.toList());
            return Strings.join(values, ',') + "...";
        } else {
            return null;
        }
    }
}
