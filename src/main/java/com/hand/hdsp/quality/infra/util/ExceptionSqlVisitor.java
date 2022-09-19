package com.hand.hdsp.quality.infra.util;

import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import lombok.Data;

/**
 * <p>
 * 异常sql访问器，修改最外层的SQLSelectItem
 * </p>
 *
 * @author lgl 2022/09/19 14:03
 * @since 1.0
 */
@Data
public class ExceptionSqlVisitor extends SQLASTVisitorAdapter {
    private Boolean isFirst = Boolean.TRUE;
    String ownerName;

    @Override
    public boolean visit(SQLSelectItem x) {
        //不是最外层，无需处理
        if (!isFirst) {
            return super.visit(x);
        }
        isFirst = false;
        getNewSqlSelectItem(x);
        return false;
    }

    private void getNewSqlSelectItem(SQLSelectItem x) {
        //清空原表达式
        x.setExpr(null);
        //清空别名
        x.setAlias(null);
        SQLPropertyExpr sqlPropertyExpr = new SQLPropertyExpr();
        sqlPropertyExpr.setOwner(ownerName);
        sqlPropertyExpr.setName("*");
        x.setExpr(sqlPropertyExpr);
    }
}
