package org.xdsp.quality.infra.util;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.xdsp.quality.api.dto.TimeRangeDTO;
import org.xdsp.quality.infra.vo.TimeRangeVO;

import java.text.SimpleDateFormat;

/**
 * description
 *
 * @author rui.jia01@hand-china.com 2020/04/02 12:43
 */
public class TimeToString {
    private TimeToString() {
        throw new IllegalStateException("util class!");
    }

    public static void timeToString(TimeRangeDTO timeRangeDTO) {
        String timeRange = timeRangeDTO.getTimeRange();
        TimeRangeVO range;
        if (StringUtils.isNotBlank(timeRangeDTO.getStartDate())
                ||StringUtils.isNotBlank(timeRangeDTO.getEndDate())) {
            range = TimeRangeVO.builder().startDate(timeRangeDTO.getStart()).endDate(timeRangeDTO.getEnd()).build();
        } else {
            range = TimeRange.getTimeRange(timeRange);
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
