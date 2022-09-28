package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.infra.constant.PredefinedParams;
import com.hand.hdsp.quality.infra.constant.SimpleTimeUnitEnum;
import com.hand.hdsp.quality.infra.dataobject.SpecifiedParamsResponseDO;
import com.hand.hdsp.quality.infra.mapper.BatchPlanMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.core.base.BaseConstants;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[\\-|0-9][0-9]*");


    public static String handlePredefinedParams(String str, BatchResultBase batchResultBase) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        Matcher matcher = PredefinedParams.PREDEFINED_PARAM_REGEX.matcher(str);
        while (matcher.find()) {
            // _p_current_user_id
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_USER_ID)) {
                //如果是任务流执行，token都是admin的token怎么考虑？
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.CURRENT_USER_ID),
                        String.valueOf(DetailsHelper.getUserDetails().getUserId()));
            }

            //_p_current_role_id
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_ROLE_ID)) {
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.CURRENT_ROLE_ID),
                        String.valueOf(DetailsHelper.getUserDetails().getRoleId()));
            }

            // _p_current_tenant_id
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_TENANT_ID)) {
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.CURRENT_TENANT_ID),
                        String.valueOf(DetailsHelper.getUserDetails().getTenantId()));
            }
            // _p_current_project_id
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_PROJECT_ID)) {
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.CURRENT_PROJECT_ID),
                        String.valueOf(batchResultBase.getProjectId()));
            }
            // _p_current_project_code
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_PROJECT_CODE)) {
                //通过项目id获取项目编码
                BatchPlanMapper planMapper = ApplicationContextHelper.getContext().getBean(BatchPlanMapper.class);
                String projectCode = planMapper.getProjectCodeById(batchResultBase.getProjectId());
                str = str.replaceAll(String.format(PARAM_PATTERN, PredefinedParams.CURRENT_PROJECT_CODE),
                        String.valueOf(projectCode));
            }
            // _p_current_data_time
            if (matcher.group(1).trim().contains(PredefinedParams.CURRENT_DATE_TIME)) {
                str = handleDateTime(str, matcher.group(1).trim(), new SpecifiedParamsResponseDO());
            }
        }
        return str;
    }

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
                str = handleLastDateTime(str, matcher.group(1).trim(), specifiedParamsResponse);
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

    private static String handleLastDateTime(String str, String matcher, SpecifiedParamsResponseDO specifiedParamsResponse) {
        DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern(BaseConstants.Pattern.DATETIME);
        // 表查不到当前时间取本地时间
        String originDataTime = Optional.ofNullable(specifiedParamsResponse.getCurrentDataTime())
                .orElse(LocalDateTime.now().format(defaultFormatter));
        String[] splitArr = matcher.split(PredefinedParams.SPLIT_KEY);
        String lastDateTime;
        if (splitArr.length == 1) {
            // _p_last_date_time
            lastDateTime = LocalDateTime.parse(originDataTime, defaultFormatter).format(defaultFormatter);
        } else {
            // xxx:xxx length>=2
            String modeFlag = splitArr[1];
            if (NUMERIC_PATTERN.matcher(modeFlag).matches()) {
                if (splitArr.length < 3) {
                    throw new IllegalArgumentException("不符合模式 _p_current_date_time:N:day");
                } else if (splitArr.length == 3) {
                    // 整数模式 _p_last_date_time:N:day
                    lastDateTime = genLocalDateTime(originDataTime, defaultFormatter, Long.valueOf(modeFlag), splitArr[2]);
                } else {
                    // 模式升级为 _p_last_date_time:N:day:日期格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(matcher.split(PredefinedParams.SPLIT_KEY, 4)[3]);
                    lastDateTime = genLocalDateTime(originDataTime, defaultFormatter, Long.valueOf(modeFlag), splitArr[2]);
                    lastDateTime = LocalDateTime.parse(lastDateTime, defaultFormatter).format(formatter);
                }
            } else {
                // 字符串模式 _p_last_date_time:yyyy-MM-dd，_p_last_date_time:yyyy-MM-dd HH:mm:ss
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(matcher.split(PredefinedParams.SPLIT_KEY,
                        2)[1]);
                lastDateTime = LocalDateTime.parse(originDataTime, defaultFormatter).format(formatter);
            }
        }
        // 替换多余的转义
        String lastDateTimeTmp = lastDateTime.replace("\\", "");
        str = str.replace(String.format("${%s}", matcher), lastDateTimeTmp);
        // 时间戳的currentDateTime需更新
        specifiedParamsResponse.setLastDateTime(lastDateTimeTmp);
        return str;
    }

    private static String handleDateTime(String str, String matcher, SpecifiedParamsResponseDO specifiedParamsResponse) {
        DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern(BaseConstants.Pattern.DATETIME);
        // 表查不到当前时间取本地时间
        String originDataTime = Optional.ofNullable(specifiedParamsResponse.getCurrentDataTime())
                .orElse(LocalDateTime.now().format(defaultFormatter));
        String[] splitArr = matcher.split(PredefinedParams.SPLIT_KEY);
        String currentDateTime;
        if (splitArr.length == 1) {
            // _p_current_date_time
            currentDateTime = LocalDateTime.parse(originDataTime, defaultFormatter).format(defaultFormatter);
        } else {
            // xxx:xxx length>=2
            String modeFlag = splitArr[1];
            if (NUMERIC_PATTERN.matcher(modeFlag).matches()) {
                if (splitArr.length < 3) {
                    throw new IllegalArgumentException("不符合模式 _p_current_date_time:N:day");
                } else if (splitArr.length == 3) {
                    // 整数模式 _p_current_date_time:N:day
                    currentDateTime = genLocalDateTime(originDataTime, defaultFormatter, Long.valueOf(modeFlag), splitArr[2]);
                } else {
                    // 模式升级为 _p_current_date_time:N:day:日期格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(matcher.split(PredefinedParams.SPLIT_KEY, 4)[3]);
                    currentDateTime = genLocalDateTime(originDataTime, defaultFormatter, Long.valueOf(modeFlag), splitArr[2]);
                    currentDateTime = LocalDateTime.parse(currentDateTime, defaultFormatter).format(formatter);
                }
            } else {
                // 字符串模式 _p_current_date_time:yyyy-MM-dd，_p_current_date_time:yyyy-MM-dd HH:mm:ss
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(matcher.split(PredefinedParams.SPLIT_KEY,
                        2)[1]);
                currentDateTime = LocalDateTime.parse(originDataTime, defaultFormatter).format(formatter);
            }
        }
        // 替换多余的转义
        String currentDateTimeTmp = currentDateTime.replace("\\", "");
        str = str.replace(String.format("${%s}", matcher), currentDateTimeTmp);
        // 时间戳的currentDateTime需更新
        specifiedParamsResponse.setCurrentDataTime(currentDateTimeTmp);
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
