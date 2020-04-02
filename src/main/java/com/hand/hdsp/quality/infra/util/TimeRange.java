package com.hand.hdsp.quality.infra.util;

import static com.hand.hdsp.quality.infra.constant.TimeRange.*;
import static com.hand.hdsp.quality.infra.constant.TimeRange.CURRENT_SEASON;

import java.util.Calendar;
import java.util.Date;

import com.hand.hdsp.quality.infra.vo.TimeRangeVO;

/**
 * 根据条件获取时间范围
 *
 * @author rui.jia01@hand-china.com 2020/04/02 9:37
 */
public class TimeRange {

    private TimeRange(){
        throw new IllegalStateException("util class!");
    }

    public static TimeRangeVO getTimeRange(String timeRange){
        switch (timeRange) {
            case TODAY :
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 0);
                TimeRangeVO timeRangeVO = TimeRangeVO.builder()
                        .startDate(cal.getTime())
                        .endDate(new Date())
                        .build();
                return timeRangeVO;
            case RECENT_24_HOUR :
                Calendar cal1 = Calendar.getInstance();
                cal1.set(Calendar.HOUR_OF_DAY,cal1.get(Calendar.HOUR_OF_DAY)-24);
                cal1.set(Calendar.SECOND, cal1.get(Calendar.SECOND));
                cal1.set(Calendar.MINUTE, cal1.get(Calendar.MINUTE));
                cal1.set(Calendar.MILLISECOND,cal1.get(Calendar.MILLISECOND));
                TimeRangeVO timeRangeVO1 = TimeRangeVO.builder()
                        .startDate(cal1.getTime())
                        .endDate(new Date())
                        .build();
                return timeRangeVO1;
            case RECENT_48_HOUR :
                Calendar cal2 = Calendar.getInstance();
                cal2.set(Calendar.HOUR_OF_DAY,cal2.get(Calendar.HOUR_OF_DAY)-48);
                cal2.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
                cal2.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
                cal2.set(Calendar.MILLISECOND,cal2.get(Calendar.MILLISECOND));
                TimeRangeVO timeRangeVO2 = TimeRangeVO.builder()
                        .startDate(cal2.getTime())
                        .endDate(new Date())
                        .build();
                return timeRangeVO2;
            case RECENT_30_DAY :
                Calendar cal3 = Calendar.getInstance();
                cal3.set(Calendar.DAY_OF_MONTH,cal3.get(Calendar.DAY_OF_MONTH)-30);
                cal3.set(Calendar.HOUR_OF_DAY,cal3.get(Calendar.HOUR_OF_DAY));
                cal3.set(Calendar.SECOND, cal3.get(Calendar.SECOND));
                cal3.set(Calendar.MINUTE, cal3.get(Calendar.MINUTE));
                cal3.set(Calendar.MILLISECOND,cal3.get(Calendar.MILLISECOND));
                TimeRangeVO timeRangeVO3 = TimeRangeVO.builder()
                        .startDate(cal3.getTime())
                        .endDate(new Date())
                        .build();
                return timeRangeVO3;
            case LAST_MONTH :
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.add(Calendar.MONTH, -1);
                startCalendar.set(Calendar.DAY_OF_MONTH, 1);
                setMinTime(startCalendar);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.add(Calendar.MONTH, -1);
                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                setMaxTime(endCalendar);
                TimeRangeVO timeRangeVO4 = TimeRangeVO.builder()
                        .startDate(startCalendar.getTime())
                        .endDate(endCalendar.getTime())
                        .build();
                return timeRangeVO4;
            case CURRENT_SEASON :
                Calendar startCalendar1 = Calendar.getInstance();
                startCalendar1.set(Calendar.MONTH, ((int) startCalendar1.get(Calendar.MONTH) / 3) * 3);
                startCalendar1.set(Calendar.DAY_OF_MONTH, 1);
                setMinTime(startCalendar1);

                Calendar endCalendar1 = Calendar.getInstance();
                endCalendar1.set(Calendar.MONTH, ((int) startCalendar1.get(Calendar.MONTH) / 3) * 3 + 2);
                endCalendar1.set(Calendar.DAY_OF_MONTH, endCalendar1.getActualMaximum(Calendar.DAY_OF_MONTH));
                setMaxTime(endCalendar1);
                TimeRangeVO timeRangeVO5 = TimeRangeVO.builder()
                        .startDate(startCalendar1.getTime())
                        .endDate(endCalendar1.getTime())
                        .build();
                return timeRangeVO5;
            case ALL :
                return null;
            default:
                return null;
        }
    }


    private static void setMinTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setMaxTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }
}
