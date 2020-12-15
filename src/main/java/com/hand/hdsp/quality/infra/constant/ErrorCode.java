package com.hand.hdsp.quality.infra.constant;

/**
 * 错误码
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 14:16
 */
public interface ErrorCode {

    /**
     * 编码已存在
     */
    String CODE_ALREADY_EXISTS = "hdsp.xqua.err.code_already_exists";

    /**
     * 规则分组父级分类ID不能为空
     */
    String PARENT_NULL = "hdsp.xqua.err.parent_null";

    /**
     * 分组下存在规则或其他分组，方案下存在行信息，或方案正在运行，无法删除
     */
    String CAN_NOT_DELETE = "hdsp.xqua.err.can_not_delete";

    /**
     * 告警等级阈值范围重叠
     */
    String WARNING_LEVEL_OVERLAP = "hdsp.xqua.err.warning_level_overlap";

    /**
     * 该校验项仅支持返回一个值
     */
    String CHECK_ITEM_ONE_VALUE = "hdsp.xqua.err.check_item_one_value";


    /**
     * 异常阻断
     */
    String EXCEPTION_BLOCK = "hdsp.xqua.err.exception_block";

    /**
     * 校验项[{0}]未找到执行程序
     */
    String CHECK_ITEM_NOT_EXIST = "hdsp.xqua.err.measure.check_item.not_exist";

    /**
     * 阈值不是一个合法的日期
     */
    String EXPECTED_VALUE_IS_NOT_DATE = "hdsp.xqua.err.expected_value_is_not_date";

    /**
     * 告警等级范围不能同时为空！
     */
    String WARNING_LEVEL_RANGE_NOT_ALL_EMPTY = "hdsp.xqua.err.warning_level_range_not_all_empty";

    String DQ_RULE_LINE_LIST_ALL = "hdsp.xqua.err.feign.ops.dq_rule_line.list_all";

    String NOT_FIND_VALUE = "hdsp.xqua.err.not_find_value";

    //DATA_STANDARD_ERROR
    String DATA_STANDARD_CODE_EXIST = "hdsp.xqua.err.data_standard_code_exist";
    String DATA_STANDARD_NAME_EXIST = "hdsp.xqua.err.data_standard_name_exist";
    String DATA_STANDARD_CAN_NOT_DELETE = "hdsp.xqua.err.data_standard_status_can_not_delete";
    String DATA_STANDARD_NOT_EXIST = "hdsp.xqua.err.data_standard_not_exist";

    // DATA_FILED_ERROR
    String DATA_FIELD_NAME_EXIST = "hdsp.xqua.err.data_field_name_already_exist";
    String DATA_FIELD_CAN_NOT_DELETE = "hdsp.xqua.err.data_field_status_can_not_delete";
    String DATA_FIELD_VERSION_NOT_EXIST = "hdsp.xqua.err.data_field_version_not_exists";
    String DATA_FIELD_STANDARD_NOT_EXIST = "hdsp.xqua.err.data_field_standard_not_exists";

    String GROUP_HAS_CHILD_GROUP = "hdsp.xqua.err.group_has_child_group";

    String GROUP_HAS_STANDARD = "hdsp.xqua.err.group_has_standard";

    String CORE_DATASOURCE_LIST = "hdsp.xqua.err.feign.core.datasource_list";

    String CORE_DATASOURCE_EXEC_SQL = "hdsp.xqua.err.feign.core.datasource_exec_sql";

    String CORE_DATASOURCE_EXEC_QUERY = "hdsp.xqua.err.feign.core.datasource_exec_query";

    String CORE_DATASOURCE_EXEC_SQL_BATCH = "hdsp.xqua.err.feign.core.datasource_exec_sql_batch";

    String CORE_DATASOURCE_DETAIL = "hdsp.xqua.err.feign.core.datasource_detail";

    String FIELD_NO_SUPPORT_CHECK_TYPE = "hdsp.xqua.err.field_no_support_check_type";

    String JOB_CREATE_UPDATE = "hdsp.xqua.err.feign.job_create_update";

    String DISPATCH_GET_JOB_LOG = "hdsp.xqua.err.feign.dispatch_execution_flow_get_job_log";

    String REST_JOB_CREATE = "hdsp.xqua.err.feign.rest_job_create";

    String REST_JOB_FIND_NAME = "hdsp.xqua.err.feign.rest_job_find_name";

    String JSON_PROCESS = "hdsp.xqua.err.json_process";

    String CREATE_OR_UPDAT_TIMESTAMP = "hdsp.xqua.err.feign.timestamp_create_or_update_timestamp";

    String GET_INCREMENT_PARAM = "hdsp.xqua.err.feign.timestamp_get_increment_param";

    String UPDATE_INCREMENT = "hdsp.xqua.err.feign.timestamp_update_increment";

    String COUNT_TYPE_EXIST = "hdsp.xqua.err.count_type_exist";

    String COUNT_TYPE_NOT_EXIST = "hdsp.xqua.err.count_type_not_exist";

    String CHECK_ITEM_EXIST = "hdsp.xqua.err.check_item_exist";

    String CONVERT_SQL = "hdsp.xqua.err.convert_sql";

    String STANDARD_AIM_EXIST= "hdsp.xqua.err.standard_aim_exist";

    String STANDARD_EXTRA_LIST_IS_EMPTY="hdsp.xqua.err.standard_extra_list_is_empty";

    String STANDARD_AIM_LIST_IS_EMPTY="hdsp.xqua.err.standard_aim_list_is_empty";

    String DATA_STANDARD_VERSION_NOT_EXIST = "hdsp.xqua.err.data_standard_version_not_exist";

    String NAME_STANDARD_PARAMS_EMPTY = "hdsp.xsta.err.is_empty";
    String NAME_STANDARD_NOT_EXIST = "hdsp.xsta.err.not_exist";
    String STANDARD_AIM_NOT_EXIST = "hdsp.xsta.err.standard_aim_not_exist";

}
