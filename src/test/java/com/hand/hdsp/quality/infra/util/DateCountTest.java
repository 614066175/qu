package com.hand.hdsp.quality.infra.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Date;

@Slf4j
public class DateCountTest {
    @Test
    public void testDateCount() {
        log.info(DateUtils.addDays(new Date(), -7).toString());
    }
}
