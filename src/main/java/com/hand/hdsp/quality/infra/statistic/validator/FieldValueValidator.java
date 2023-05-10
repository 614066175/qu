package com.hand.hdsp.quality.infra.statistic.validator;

import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.StandardValueType.*;

/**
 * <p>
 * 字段值校验器，（数据格式，值域）
 * </p>
 *
 * @author lgl 2022/8/1 11:11
 * @since 1.0
 */
@Component
@Order(2)
public class FieldValueValidator implements StatisticValidator {
    private final DriverSessionService driverSessionService;

    public FieldValueValidator(DriverSessionService driverSessionService) {
        this.driverSessionService = driverSessionService;
    }

    @Override
    public boolean valid(DataFieldDTO dataFieldDTO, StandardAimDTO standardAimDTO, AimStatisticsDTO aimStatisticsDTO) {
        //获取所有的数据进行数据值校验
        DriverSession driverSession = driverSessionService.getDriverSession(standardAimDTO.getTenantId(), standardAimDTO.getDatasourceCode());
        //todo 分批检验
        //获取字段
        String fieldName = standardAimDTO.getFieldName();
        if (fieldName.contains("(")) {
            fieldName = fieldName.substring(0, fieldName.indexOf("("));
        }
        List<Map<String, Object>> result = driverSession.executeOneQuery(standardAimDTO.getSchemaName(), String.format("select %s from %s", fieldName, standardAimDTO.getTableName()));
        //如果都为空，无需校验，直接返回
        if (CollectionUtils.isEmpty(result)) {
            aimStatisticsDTO.setCompliantRow(0L);
            return true;
        }
        if (StringUtils.isEmpty(dataFieldDTO.getDataPattern()) && StringUtils.isEmpty(dataFieldDTO.getValueType())) {
            //都为空无需校验，全部合规
            aimStatisticsDTO.setCompliantRow((long) result.size());
            return true;
        }
        String finalFieldName = fieldName;
        AtomicReference<Long> compliantRow = new AtomicReference<>(0L);
        result.forEach(map -> {
            String value = map.get(finalFieldName) == null ? StringUtils.EMPTY: String.valueOf(map.get(finalFieldName));
            //对值进行校验
            if (StringUtils.isNotEmpty(dataFieldDTO.getDataPattern())) {
                Pattern pattern = Pattern.compile(dataFieldDTO.getDataPattern());
                Matcher matcher = pattern.matcher(value);
                if (!matcher.find()) {
                    //不匹配直接返回
                    return;
                }
            }
            // todo 值域类型校验
            if (StringUtils.isNotEmpty(dataFieldDTO.getValueType()) && StringUtils.isNotEmpty(dataFieldDTO.getValueRange())) {
                switch (dataFieldDTO.getValueType()) {
                    case AREA:
                        break;
                    case ENUM:
                        break;
                    case VALUE_SET:
                        break;
                    case LOV_VIEW:
                        break;
                    case REFERENCE_DATA:
                        break;
                    default:
                        throw new CommonException("not support value type");
                }
            }
            compliantRow.getAndSet(compliantRow.get() + 1);
        });
        aimStatisticsDTO.setCompliantRow(compliantRow.get());
        return true;
    }
}
