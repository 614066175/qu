package org.xdsp.quality.infra.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>方案常量类</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface PlanConstant {

    /**
     * redis key
     */
    String CACHE_BUCKET_EXCEPTION = "xqua:exception";

    /**
     * 数据质量任务类型
     */
    String JOB_TYPE = "QUALITY";

    /**
     * 数据质量任务分类
     */
    String JOB_CLASS = "QUALITY";


    /**
     * 数据质量任务方法
     */
    String JOB_METHOD = "GET";

    /**
     * 数据质量任务级别
     */
    String JOB_LEVEL = "AUTO";

    /**
     * 默认主题ID
     */
    Long DEFAULT_THEME_ID = -7L;

    /**
     * 默认层次ID
     */
    Long DEFAULT_LAYER_ID = -7L;

    /**
     * 告警等级LOV
     */
    String LOV_WARNING_LEVEL = "XQUA.WARNING_LEVEL";

    /**
     * 方案状态LOV
     */
    String LOV_PLAN_STATUS = "XQUA.PLAN_STATUS";

    /**
     * 校验项(总体)LOV
     */
    String LOV_CHECK_ITEM = "XQUA.CHECK_ITEM";

    /**
     * 校验类型 LOV
     */
    String LOV_COUNT_TYPE = "XQUA.COUNT_TYPE";

    /**
     * 比较方式 LOV
     */
    String LOV_COMPARE_WAY = "XQUA.COMPARE_WAY";

    /**
     * 比较符
     */
    String LOV_COMPARE_SYMBOL = "XQUA.COMPARE_SYMBOL";

    /**
     * 告警等级-正常
     */
    String WARNING_LEVEL_NORMAL = "NORMAL";

    /**
     * 数据质量任务名称
     */
    String JOB_NAME = "DQ_%s";

    String OLD_JOB_NAME = "DQ%d_%d_%s";


    /**
     * 数据质量时间戳类型
     */
    String TIMESTAMP_TYPE = "QUA_%d_%s_%d";

    /**
     * 通用SQL
     */
    String COMMON_SQL = "COMMON_SQL";

    /**
     * 默认页大小
     */
    int DEFAULT_SIZE = 10000;

    /**
     * 比较方式
     */
    interface CompareWay {
        /**
         * 绝对值
         */
        String ABSOLUTE_VALUE = "ABSOLUTE_VALUE";
        /**
         * 上升
         */
        String RISE_RATE = "RISE_RATE";
        /**
         * 下降
         */
        String DOWN_RATE = "DOWN_RATE";
        /**
         * 范围比较
         */
        String RANGE = "RANGE";
        /**
         * 值比较
         */
        String VALUE = "VALUE";
    }

    /**
     * 方案状态
     */
    interface PlanStatus {
        /**
         * 未评估
         */
        String NOTASSESS = "NOTASSESS";
        /**
         * 运行中
         */
        String RUNNING = "RUNNING";
        /**
         * 执行成功
         */
        String SUCCESS = "SUCCESS";
        /**
         * 执行失败
         */
        String FAILED = "FAILED";

    }

    /**
     * 数据质量规则表规则类型
     */
    interface RuleType {
        /**
         * 表间关系
         */
        String TABLE_RELATION = "TABLE_RELATION";

        /**
         * 表级
         */
        String TABLE_TYPE = "TABLE_TYPE";

        /**
         * 自定义SQL
         */
        String SQL_CUSTOM = "SQL_CUSTOM";

    }


    /**
     * 批数据方案结果表-规则类型
     */
    interface ResultRuleType {
        String TABLE = "TABLE";
        String FIELD = "FIELD";
        String REL_TABLE = "REL_TABLE";
    }


    /**
     * 规则校验方式 XQUA.CHECK_WAY
     */
    interface CheckWay {
        /**
         * 常规校验
         */
        String COMMON = "COMMON";
        /**
         * 正则表达式
         */
        String REGULAR = "REGULAR";
    }

    /**
     * 比较符号
     */
    interface CompareSymbol {
        /**
         * 等于
         */
        String EQUAL = "EQUAL";
        /**
         * 不等于
         */
        String NOT_EQUAL = "NOT_EQUAL";
        /**
         * 包含
         */
        String INCLUDED = "INCLUDED";
        /**
         * 不包含
         */
        String NOT_INCLUDED = "NOT_INCLUDED";
    }

    /**
     * 比较符号
     */
    interface IncrementStrategy {
        /**
         * 无,全量同步
         */
        String NONE = "NONE";
        /**
         * 按时间增量
         */
        String DATE = "DATE";
        /**
         * 按ID增量
         */
        String ID = "ID";
    }

    interface CheckType {
        /**
         * 标准规范
         */
        String STANDARD = "STANDARD";
    }

    /**
     * 校验项
     */
    interface CheckItem {
        /**
         * 表行数
         */
        String TABLE_LINE = "TABLE_LINE";
        /**
         * 一致性
         */
        String CONSISTENCY = "CONSISTENCY";

        /**
         * 一致性
         */
        String DATA_LENGTH = "DATA_LENGTH";
        /**
         * 字段值
         */
        String FIELD_VALUE = "FIELD_VALUE";
        /**
         * 正则
         */
        String REGULAR = "REGULAR";

        /**
         * accuracy rate 准确率
         */
        String ACCURACY_RATE = "ACCURACY_RATE";

        /**
         * 计算值比较 calculated value
         */
        String CALCULATED_VALUE = "CALCULATED_VALUE";

        /**
         * 空值个数
         */
        String FIELD_EMPTY = "FIELD_EMPTY";

        /**
         * 空值个数
         */
        String FIELD_UNIQUE = "FIELD_UNIQUE";
    }

    /**
     * 校验类型
     */
    interface CountType {
        /**
         * 值集
         */
        String LOV_VALUE = "LOV_VALUE";

        /**
         * 固定值
         */
        String FIXED_VALUE = "FIXED_VALUE";
        /**
         * 枚举值
         */
        String ENUM_VALUE = "ENUM_VALUE";
        /**
         * 逻辑值
         */
        String LOGIC_VALUE = "LOGIC_VALUE";

        String LENGTH_RANGE = "LENGTH_RANGE";

        String REFERENCE_DATA = "REFERENCE_DATA";

    }

    /**
     * SQL类型
     */
    interface SqlType {
        /**
         * 查询SQL
         */
        String SQL = "SQL";

        /**
         * TABLE
         */
        String TABLE = "TABLE";

    }

    /**
     * 模板SQL类型
     */
    interface TemplateSqlTag {
        String JAVA = "Java";
        String SQL = "SQL";
    }

    interface StandardValueType {
        // 区间
        String AREA = "AREA";
        // 枚举
        String ENUM = "ENUM";
        // 独立值集
        String VALUE_SET = "VALUE_SET";
        // 值集视图
        String LOV_VIEW = "LOV_VIEW";
        // 参考数据
        String REFERENCE_DATA = "REFERENCE_DATA";

        // 值域类型验证正则表达式
        Map<String, Pattern> ValueTypeRegexPattern = new HashMap<String,Pattern>(){
            {
                // {-1.1,-2.1} {-1,2} {1,2} {,}
                put(PlanConstant.StandardValueType.AREA,Pattern.compile("^(-?\\d+(\\.\\d+)?)?,(-?\\d+(\\.\\d+)?)?$"));
            }
        };
    }

    interface StandardStatus {
        String ONLINE = "ONLINE";
        String ONLINE_APPROVING = "ONLINE_APPROVING";
        String OFFLINE = "OFFLINE_APPROVING";
        String OFFLINE_APPROVING = "OFFLINE_APPROVING";
    }

    interface ExceptionParam {
        String EXCEPTION_INFO = "#exceptionInfo";
        //自定义主键用于一条数据多个异常时，进行异常合并
        String PK = "#pk";
        String RULE_NAME = "#ruleName";
        String WARNING_LEVEL = "#warningLevel";
        String PLAN_BASE_ID = "#planBaseId";
        String RESULT_BASE_ID = "#resultBaseId";
    }
}
