package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.infra.vo.TimeRangeVO;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;

import java.text.SimpleDateFormat;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/02 12:43
 */
public class TimeToString {

    public static void timeToString(TimeRangeDTO timeRangeDTO) {
        String timeRange = timeRangeDTO.getTimeRange();
        TimeRangeVO range;
        if (StringUtils.isNotBlank(timeRange)) {
            range = TimeRange.getTimeRange(timeRange);
        } else {
            range = TimeRangeVO.builder().startDate(timeRangeDTO.getStart()).endDate(timeRangeDTO.getEnd()).build();
        }
        SimpleDateFormat sf = new SimpleDateFormat(BaseConstants.Pattern.DATETIME);
        if (range != null) {
            if (range.getStartDate() != null) {
                timeRangeDTO.setStartDate(sf.format(range.getStartDate()));
            }
            if (range.getEndDate() != null) {
                timeRangeDTO.setEndDate(sf.format(range.getEndDate()));
            }
        }
    }

}
