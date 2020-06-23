package com.hand.hdsp.quality.infra.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class SqlParserTest {
    @Test
    public void testDateCount() {
        List<Map<String, Object>> fields = SqlParser.getFields(" SELECT\n" +
                "        xbrb.object_name,\n" +
                "        xbpb.datasource_id,\n" +
                "        xbpb.datasource_schema,\n" +
                "        COUNT(distinct xbrr.result_rule_id) exception_rule_count,\n" +
                "        COUNT(distinct xbri.plan_line_id) exception_item_count,\n" +
                "        COUNT(*) exception_warn_count\n" +
                "        FROM\n" +
                "        xqua_batch_result_item xbri\n" +
                "        JOIN xqua_batch_result_rule xbrr ON xbri.result_rule_id = xbrr.result_rule_id\n" +
                "        JOIN xqua_batch_result_base xbrb ON xbrr.result_base_id = xbrb.result_base_id\n" +
                "        JOIN xqua_batch_plan_base xbpb ON xbrb.plan_base_id = xbpb.plan_base_id\n" +
                "        WHERE\n" +
                "        xbrr.result_flag = 0\n" +
                "        GROUP BY\n" +
                "        xbrb.object_name,\n" +
                "        xbpb.datasource_id,\n" +
                "        xbpb.datasource_schema");
        log.info(fields.toString());

    }
}
