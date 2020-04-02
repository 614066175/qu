package com.hand.hdsp.quality.infra.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hand.hdsp.quality.infra.vo.StringTimeVO;
import com.hand.hdsp.quality.infra.vo.TimeRangeVO;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/02 12:43
 */
public class TimeToString {

    public static StringTimeVO timeToSring(String timeRange, Date startDate, Date endDate){
        TimeRangeVO range;
        if (timeRange != null && timeRange.length() > 0){
            range = TimeRange.getTimeRange(timeRange);
        } else {
            range = TimeRangeVO.builder().startDate(startDate).endDate(endDate).build();
        }
        String start = null;
        String end = null;
        if (range != null){
            if (range.getStartDate() != null){
                start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(range.getStartDate());
            }
            if (range.getEndDate() != null){
                end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(range.getEndDate());
            }
        }
        StringTimeVO stringTimeVO = StringTimeVO.builder().start(start).end(end).build();
        return stringTimeVO;
    }

}
