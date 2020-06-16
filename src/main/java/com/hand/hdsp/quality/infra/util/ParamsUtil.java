package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.infra.constant.PredefinedParams;
import com.hand.hdsp.quality.infra.constant.SimpleTimeUnitEnum;
import com.hand.hdsp.quality.infra.dataobject.SpecifiedParamsResponseDO;
import org.hzero.core.base.BaseConstants;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * <p>
 * 参数处理工具类
 * </p>
 *
 * @author abigballofmud 2020/01/10 15:54
 * @since 1.0
 */
public class ParamsUtil {

    private ParamsUtil() {
        throw new IllegalStateException("context class");
    }

    private static final String PARAM_PATTERN = "\\$\\{%s\\}";

    /**
     * 对内置参数的处理
     *
     * @param str                     替换的文本
     * @param specifiedParamsResponse az执行目录
     * @return java.lang.String
     */
    public static String handlePredefinedParams(String str, SpecifiedParamsResponseDO specifiedParamsResponse) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        Matcher matcher = PredefinedParams.PREDEFINED_PARAM_REGEX.matcher(str);
        while (matcher.find()) {
            // _p_current_data_time
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_DATE_TIME)) {
                str = handleDateTime(str, matcher.group(1).trim(), specifiedParamsResponse);
            }

            // _p_last_date_time
            if (matcher.group(1).trim().contains(PredefinedParams.LAST_DATE_TIME)) {
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.LAST_DATE_TIME),
                        specifiedParamsResponse.getLastDateTime());
            }

            // _p_current_max_id
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_MAX_ID)) {
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.CURRENT_MAX_ID),
                        specifiedParamsResponse.getCurrentMaxId());
            }
            // _p_last_max_id
            if (matcher.group(1).trim().contains(PredefinedParams.LAST_MAX_ID)) {
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.LAST_MAX_ID),
                        specifiedParamsResponse.getLastMaxId());
            }
        }
        return str;
    }

    private static String handleDateTime(String str, String matcher, SpecifiedParamsResponseDO specifiedParamsResponse) {
        DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern(BaseConstants.Pattern.DATETIME);
        // 表查不到当前时间取本地时间
        String originDataTime = Optional.ofNullable(specifiedParamsResponse.getCurrentDataTime())
                .orElse(LocalDateTime.now().format(defaultFormatter));
        String[] splitArr = matcher.split(PredefinedParams.SPLIT_KEY);
        int defaultSize = 2;
        String currentDateTime;
        if (splitArr.length > defaultSize) {
            //  _p_current_date_time:N:day
            currentDateTime = genLocalDateTime(originDataTime, defaultFormatter, Long.valueOf(splitArr[1]), splitArr[2]);
            str = str.replaceAll(String.format("\\$\\{%s\\:%s\\:%s\\}", splitArr[0], splitArr[1], splitArr[2]), currentDateTime);
        } else if (splitArr.length == defaultSize) {
            // _p_current_date_time:yyyy-MM-dd HH:mm:ss
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(splitArr[1]);
            currentDateTime = LocalDateTime.parse(originDataTime, formatter).format(formatter);
            str = str.replaceAll(String.format("\\$\\{%s\\:%s\\}", splitArr[0], splitArr[1]), currentDateTime);
        } else {
            // _p_current_date_time
            currentDateTime = LocalDateTime.parse(originDataTime, defaultFormatter).format(defaultFormatter);
            str = str.replaceAll(String.format(PARAM_PATTERN, splitArr[0]), currentDateTime);
        }
        // 时间戳的currentDateTime需更新
        specifiedParamsResponse.setCurrentDataTime(currentDateTime);
        return str;
    }

    private static String genLocalDateTime(String originDataTime, DateTimeFormatter defaultFormatter, Long interval, String unit) {
        LocalDateTime localDateTime = LocalDateTime.parse(originDataTime, defaultFormatter);
        String currentDateTime;
        switch (SimpleTimeUnitEnum.valueOf(unit.toUpperCase())) {
            case YEAR:
                currentDateTime = localDateTime.plusYears(interval).format(defaultFormatter);
                break;
            case MONTH:
                currentDateTime = localDateTime.plusMonths(interval).format(defaultFormatter);
                break;
            case WEEK:
                currentDateTime = localDateTime.plusWeeks(interval).format(defaultFormatter);
                break;
            case DAY:
                currentDateTime = localDateTime.plusDays(interval).format(defaultFormatter);
                break;
            case HOUR:
                currentDateTime = localDateTime.plusHours(interval).format(defaultFormatter);
                break;
            case MIN:
                currentDateTime = localDateTime.plusMinutes(interval).format(defaultFormatter);
                break;
            case SEC:
                currentDateTime = localDateTime.plusSeconds(interval).format(defaultFormatter);
                break;
            default:
                throw new IllegalArgumentException(String.format("非法单位[%s]，单位[year，month，week，day，hour，min，sec]", unit));
        }
        return currentDateTime;
    }

}
