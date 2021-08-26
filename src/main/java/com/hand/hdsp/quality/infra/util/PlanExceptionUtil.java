package com.hand.hdsp.quality.infra.util;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.ExceptionParam.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.starter.driver.core.infra.meta.PrimaryKey;
import org.hzero.starter.driver.core.session.DriverSession;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/29 14:03
 * @since 1.0
 */
public class PlanExceptionUtil {

    /**
     * 根据base表名查询不符合标准的数据，然后将结果设置到param对象上
     * @param param
     * @param tableName
     * @param sql
     * @param driverSession
     * @param warningLevelDTO
     */
    public static void getPlanException(MeasureParamDO param, String tableName, String sql, DriverSession driverSession, WarningLevelDTO warningLevelDTO){
        if(Strings.isEmpty(sql)){
            throw new CommonException(ErrorCode.SQL_IS_EMPTY);
        }
        sql=String.format("select %s.* %s",tableName,sql.substring(sql.indexOf("from")));
        List<PrimaryKey> primaryKeys = driverSession.tablePk(param.getSchema(), tableName);
        List<Map<String, Object>> exceptionMapList = driverSession.executeOneQuery(param.getSchema(), sql);
        if(CollectionUtils.isNotEmpty(exceptionMapList)){
            //每一条异常数据存上规则名和异常信息
            exceptionMapList.forEach(map -> {
                //存储主键用于合并异常信息
                if(CollectionUtils.isNotEmpty(primaryKeys)){
                    Set<String> columns = map.keySet();
                    List<String> columnPkList = primaryKeys.stream().map(PrimaryKey::getColumnName).collect(Collectors.toList());
                    List<String> keys = columns.stream().filter(columnPkList::contains).collect(Collectors.toList());
                    List<String> values = keys.stream().map(key -> String.valueOf(map.get(key))).collect(Collectors.toList());
                    map.put(PK,String.join("-",values));
                }else{
                    List<String> values = map.values().stream().map(String::valueOf).collect(Collectors.toList());
                    //将所有的数据
                    map.put(PK,String.join("-",values));
                }
                map.put(RULE_NAME,param.getBatchResultRuleDTO().getRuleName());
                map.put(WARNING_LEVEL,warningLevelDTO.getWarningLevel());
                //异常信息 name 数据长度 满足告警等级【大于5小于10】
                StringBuilder warningCondition=new StringBuilder();
                if(Strings.isNotEmpty(warningLevelDTO.getStartValue())){
                    warningCondition.append(String.format("大于%s",warningLevelDTO.getStartValue()));
                }
                if(Strings.isNotEmpty(warningLevelDTO.getEndValue())){
                    warningCondition.append(String.format("小于%s",warningLevelDTO.getEndValue()));
                }
                if(Strings.isNotEmpty(warningLevelDTO.getExpectedValue())){
                    warningCondition.append(String.format("等于%s",warningLevelDTO.getExpectedValue()));
                }
                String exception = String.format("【%s】 %s 满足告警条件 %s",
                        param.getFieldName(),
                        String.format("%s %s", param.getCheckItem(), param.getCountType()),
                        Strings.isNotEmpty(warningCondition) ? String.format("【%s】", warningCondition) : "");
                map.put(EXCEPTION_INFO,String.format("%s(%s)",warningLevelDTO.getWarningLevel(),exception));
            });
        }
        //直接set的话，同一个字段的不同告警规则会被覆盖掉
        param.getExceptionMapList().addAll(exceptionMapList);
    }

}
