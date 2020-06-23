package com.hand.hdsp.quality.infra.util;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * SQL解析工具类
 *
 * @author xiaoqiang
 */
@Slf4j
public class SqlParser {

    private SqlParser() {
        throw new IllegalStateException("util class");
    }

    /**
     * 获取字段列表
     *
     * @param sql sql
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> getFields(String sql) {
        final List<Map<String, Object>> fieldList = new ArrayList<>();
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            if (select == null) {
                return Collections.emptyList();
            }
            // 获取SQL实体类
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            if (plainSelect != null && CollectionUtils.isNotEmpty(plainSelect.getSelectItems())) {
                for (SelectItem selectItem : plainSelect.getSelectItems()) {
                    selectItem.accept(new SelectItemVisitorAdapter() {
                        @Override
                        public void visit(SelectExpressionItem selectExpressionItem) {
                            Map<String, Object> map = new HashMap<>();
                            String fieldName = selectExpressionItem.getExpression().toString();
                            if (fieldName != null && fieldName.contains(".")) {
                                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
                            }
                            map.put("fieldName", (selectExpressionItem.getAlias() == null ?
                                    fieldName : selectExpressionItem.getAlias().getName()));
                            map.put("columnName", selectExpressionItem.getExpression().toString());
                            fieldList.add(map);
                        }
                    });
                }
            }
        } catch (JSQLParserException e) {
            log.error("parse sql error", e);
        }
        return fieldList;
    }

}
