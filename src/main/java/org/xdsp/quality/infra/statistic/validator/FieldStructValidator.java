package org.xdsp.quality.infra.statistic.validator;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.infra.meta.Column;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.AimStatisticsDTO;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.StandardAimDTO;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.util.TypeUtil;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 字段结构校验
 * 类型，长度，精度检验。对于元数据本身进行检验，无需查询数据去校验
 * </p>
 *
 * @author lgl 2022/6/16 17:02
 * @since 1.0
 */
@Component
@Order(1)
public class FieldStructValidator implements StatisticValidator {
    private static final String FIELD_TYPE = "XMOD.FIELD_TYPE";
    private final DriverSessionService driverSessionService;

    public FieldStructValidator(DriverSessionService driverSessionService) {
        this.driverSessionService = driverSessionService;
    }

    @Override
    public boolean valid(DataFieldDTO dataFieldDTO, StandardAimDTO standardAimDTO, AimStatisticsDTO aimStatisticsDTO) {
        //获取字段标准的类型，字段标准类型不是数据库中的真实类型
        DriverSession driverSession = driverSessionService.getDriverSession(standardAimDTO.getTenantId(), standardAimDTO.getDatasourceCode());
        List<Column> columnList = driverSession.columnMetaData(standardAimDTO.getSchemaName(), standardAimDTO.getTableName());
        String filedName = standardAimDTO.getFieldName().split("\\(")[0];
        Optional<Column> first = columnList.stream().filter(column -> column.getColumnName().equalsIgnoreCase(filedName))
                .findFirst();
        Column column;
        if (first.isPresent()) {
            column = first.get();
        } else {
            throw new CommonException(ErrorCode.COLUMN_NOT_FOUND);
        }
        //检验长度，精度，类型
        //整形长度没意义
        if (StringUtils.isNotEmpty(dataFieldDTO.getFieldLength())) {
            if ("DECIMAL".equals(dataFieldDTO.getFieldType()) || "STRING".equals(dataFieldDTO.getFieldType())) {
                Integer fieldLength = Integer.parseInt(dataFieldDTO.getFieldLength());
                if (!fieldLength.equals(column.getColumnSize())) {
                    //无需继续检验
                    aimStatisticsDTO.setValidFlag(false);
                    aimStatisticsDTO.setCompliantRow(0L);
                    return false;
                }
            }
        }
        //精度
        if ("DECIMAL".equals(dataFieldDTO.getFieldType())) {
            if (!dataFieldDTO.getFieldAccuracy().equals(column.getDecimalDigits())) {
                //无需继续检验
                aimStatisticsDTO.setValidFlag(false);
                aimStatisticsDTO.setCompliantRow(0L);
                return false;
            }
        }

        //类型校验
        String type = driverSession.formatColumnType(column.getTypeName());
        if (!type.equals(dataFieldDTO.getFieldType())) {
            //无需继续检验
            aimStatisticsDTO.setValidFlag(false);
            aimStatisticsDTO.setCompliantRow(0L);
            return false;
        }

        return false;
    }
}
