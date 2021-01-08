package com.hand.hdsp.quality.infra.util;

import java.util.List;
import java.util.Map;

import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
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
        List<Map<String, Object>> exceptionMapList = driverSession.executeOneQuery(param.getSchema(), sql);
        if(CollectionUtils.isNotEmpty(exceptionMapList)){
            //每一条异常数据存上规则名和异常信息
            exceptionMapList.forEach(map -> {
                map.put("$ruleName",param.getBatchResultRuleDTO().getRuleName());
                map.put("$warningLevel",warningLevelDTO.getWarningLevel());
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
                map.put("$exceptionInfo",String.format("【%s】 %s 满足告警条件【%s】",
                        param.getFieldName(),
                        String.format("%s %s",param.getCheckItem(),param.getCountType()),
                        warningCondition
                ));
            });
        }
        param.setExceptionMapList(exceptionMapList);
    }

}
