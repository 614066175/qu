package org.xdsp.quality.infra.constant;

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
    String CODE_ALREADY_EXISTS = "xqua.err.code_already_exists";

    /**
     * 规则分组父级分类ID不能为空
     */
    String PARENT_NULL = "xqua.err.parent_null";

    /**
     * 分组下存在规则或其他分组，方案下存在行信息，或方案正在运行，无法删除
     */
    String CAN_NOT_DELETE = "xqua.err.can_not_delete";

    /**
     * 告警等级阈值范围重叠
     */
    String WARNING_LEVEL_OVERLAP = "xqua.err.warning_level_overlap";

    /**
     * 该校验项仅支持返回一个值
     */
    String CHECK_ITEM_ONE_VALUE = "xqua.err.check_item_one_value";


    /**
     * 异常阻断
     */
    String EXCEPTION_BLOCK = "xqua.err.exception_block";

    /**
     * 校验项[{0}]未找到执行程序
     */
    String CHECK_ITEM_NOT_EXIST = "xqua.err.measure.check_item.not_exist";

    /**
     * 阈值不是一个合法的日期
     */
    String EXPECTED_VALUE_IS_NOT_DATE = "xqua.err.expected_value_is_not_date";

    /**
     * 告警等级范围不能同时为空！
     */
    String WARNING_LEVEL_RANGE_NOT_ALL_EMPTY = "xqua.err.warning_level_range_not_all_empty";

    String DQ_RULE_LINE_LIST_ALL = "xqua.err.feign.ops.dq_rule_line.list_all";

    String NOT_FIND_VALUE = "xqua.err.not_find_value";
    String NOT_FIND_REFERENCE_DATA_VALUE = "xqua.err.not_find_reference_data_value";

    //DATA_STANDARD_ERROR
    String DATA_STANDARD_CODE_EXIST = "xqua.err.data_standard_code_exist";
    String DATA_STANDARD_NAME_EXIST = "xqua.err.data_standard_name_exist";
    String DATA_STANDARD_CAN_NOT_DELETE = "xqua.err.data_standard_status_can_not_delete";
    String DATA_STANDARD_NOT_EXIST = "xqua.err.data_standard_not_exist";

    // DATA_FILED_ERROR
    String DATA_FIELD_NAME_EXIST = "xqua.err.data_field_name_already_exist";
    String DATA_FIELD_CAN_NOT_DELETE = "xqua.err.data_field_status_can_not_delete";
    String DATA_FIELD_VERSION_NOT_EXIST = "xqua.err.data_field_version_not_exists";
    String DATA_FIELD_STANDARD_NOT_EXIST = "xqua.err.data_field_standard_not_exists";

    String GROUP_HAS_CHILD_GROUP = "xqua.err.group_has_child_group";

    String GROUP_HAS_STANDARD = "xqua.err.group_has_standard";

    String CORE_DATASOURCE_LIST = "xqua.err.feign.core.datasource_list";

    String CORE_DATASOURCE_EXEC_SQL = "xqua.err.feign.core.datasource_exec_sql";

    String CORE_DATASOURCE_EXEC_QUERY = "xqua.err.feign.core.datasource_exec_query";

    String CORE_DATASOURCE_EXEC_SQL_BATCH = "xqua.err.feign.core.datasource_exec_sql_batch";

    String CORE_DATASOURCE_DETAIL = "xqua.err.feign.core.datasource_detail";

    String FIELD_NO_SUPPORT_CHECK_TYPE = "xqua.err.field_no_support_check_type";

    String JOB_CREATE_UPDATE = "xqua.err.feign.job_create_update";

    String DISPATCH_GET_JOB_LOG = "xqua.err.feign.dispatch_execution_flow_get_job_log";

    String REST_JOB_CREATE = "xqua.err.feign.rest_job_create";

    String REST_JOB_FIND_NAME = "xqua.err.feign.rest_job_find_name";

    String JSON_PROCESS = "xqua.err.json_process";

    String CREATE_OR_UPDAT_TIMESTAMP = "xqua.err.feign.timestamp_create_or_update_timestamp";

    String GET_INCREMENT_PARAM = "xqua.err.feign.timestamp_get_increment_param";

    String UPDATE_INCREMENT = "xqua.err.feign.timestamp_update_increment";

    String COUNT_TYPE_EXIST = "xqua.err.count_type_exist";

    String COUNT_TYPE_NOT_EXIST = "xqua.err.count_type_not_exist";

    String CHECK_ITEM_EXIST = "xqua.err.check_item_exist";

    String CONVERT_SQL = "xqua.err.convert_sql";

    String STANDARD_AIM_EXIST = "xqua.err.standard_aim_exist";

    String STANDARD_EXTRA_LIST_IS_EMPTY = "xqua.err.standard_extra_list_is_empty";

    String STANDARD_AIM_LIST_IS_EMPTY = "xqua.err.standard_aim_list_is_empty";

    String DATA_STANDARD_VERSION_NOT_EXIST = "xqua.err.data_standard_version_not_exist";

    String NAME_STANDARD_PARAMS_EMPTY = "xsta.err.is_empty";
    String NAME_STANDARD_NOT_EXIST = "xsta.err.not_exist";
    String STANDARD_AIM_NOT_EXIST = "xsta.err.standard_aim_not_exist";


    String BATCH_PLAN_FIELD_NOT_EXIST = "xqua.err.batch_plan_field_not_exist";
    String BATCH_PLAN_BASE_NOT_EXIST = "xqua.err.batch_plan_base_not_exist";

    String LOV_CODE_NOT_EXIST = "xqua.err.lov_code_not_exist";

    String SQL_IS_EMPTY = "xqua.err.sql_is_empty";

    String BATCH_RESULT_NOT_EXIST = "xqua.err.batch_result_not_exist";
    String PLAN_BASE_ID_IS_EMPTY = "xqua.err.plan_base_id_is_empty";
    String PAGE_ERROR = "xqua.err.page_error";
    String EXCEPTION_PARAM_ERROR = "xqua.err.exception_param_error";

    String PROBLEM_HAS_CHILD_PROBLEM = "xqua.err.problem_has_child_problem";

    String CHECK_RULE_IS_ERROR = "xqua.err.check_rule_is_error";

    String UNKNOWN_DATATYPE = "xqua.err.unknown_datatype";

    String LINEAGE_ERROR = "xqua.err.lineage_error";

    String DOC_STANDARD_NAME_ALREADY_EXIST = "xqua.err.doc_standard_name_already_exist";

    String NAME_STANDARD_NAME_ALREADY_EXIST = "xqua.err.name_standard_name_already_exist";

    String GROUP_NAME_ALREADY_EXIST = "xqua.err.group_name_already_exist";

    String FILE_DOWNLOAD_FAIL = "xqua.err.file_download_fail";

    String DOC_NO_FILE = "xqua.err.doc_no_file";

    String LOC_NO_FILE = "xqua.err.loc.is_empty";

    String WORK_ORDER_STATUS_ERROR = "xqua.err.work_order_status_error";

    String WORK_ORDER_STATUS_CAN_NOT_REVOKE = "xqua.err.work_order_status_can_not_revoke";

    String WORK_ORDER_NOT_EXIST = "xqua.err.work_order_not_exist";

    String WORK_ORDER_ALREADY_LAUNCH = "xqua.err.work_order_already_launch";

    String WORK_ORDER_SOLUTION_CAN_NOT_NULL = "xqua.err.work_order_solution_can_not_null";

    String STANDARD_TEAM_NOT_EXIST = "xsta.standard_team_not_exist";

    String STANDARD_TEAM_IS_INHERITED = "xsta.standard_team_is_inherited";

    String PLAN_GROUP_NOT_EXIST = "xqua.plan_group_not_exist";

    String CORE_CUSTOMTABLE_COLUMN_LIST = "xqua.err.feign_model_customtable_column_list";

    String CORE_CUSTOMTABLE_LIST = "xqua.err.feign_model_customtable_list";
    String DATA_LENGTH_CAN_NOT_NULL = "xsta.data_length_can_not_null";

    String VALUE_RANGE_CAN_NOT_NULL = "xsta.value_range_can_not_null";

    String STANDARD_NO_AIM = "xqua.err.standard_no_aim";


    String EXCEL_WRITE_ERROR = "xqua.err.excel_write";
    String EXCEL_READ_ERROR = "xqua.err.excel_read";

    String PLAN_NOT_EXIST = "xqua.err.plan_not_exist";

    /**
     * 分组或子分组下存在评估方案
     */
    String EXISTS_OTHER_PLAN = "xqua.err.plan_exists_other_plan";

    String NO_APPROVAL_INSTANCE = "xqua.err.no_approval_instance";

    /**
     * 分组或子分组下存在标准规则
     */
    String EXISTS_OTHER_GROUP_OR_RULE = "xqua.err.exists_other_group_or_rule";
    /**
     * 文件格式不支持
     */
    String FILE_TYPE_NOT_SUPPORTED = "xqua.err.file_type_not_supported";


    String ROOT_EN_SHORT_EXIST = "xqua.err.root_en_short_exist";

    String ROOT_NAME_EXIST = "xqua.err.root_name_exist";

    String ROOT_NOT_DELETE = "xsta.err.root_can_not_delete";

    String ROOT_EN_SHORT_ERROR = "xsta.err.root_en_short_error";

    String ROOT_NOT_EXIST = "xqua.err.root_not_exists";

    String GROUP_HAS_ROOT = "xqua.err.group_has_root";

    String USER_NO_EMPLOYEE = "xqua.err.user_no_employee";

    String DATA_IN_PROCESS = "xqua.err.data_in_process";

    String DATA_NO_VALUE = "xqua.err.data_no_value";
}
