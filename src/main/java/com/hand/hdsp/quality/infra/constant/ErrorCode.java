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
    String CODE_ALREADY_EXISTS = "hdp.error.code_already_exists";

    /**
     * 规则分组父级分类ID不能为空
     */
    String PARENT_NULL = "error.parent_null";

    /**
     * 分组下存在规则或其他分组，方案下存在行信息，或方案正在运行，无法删除
     */
    String CAN_NOT_DELETE = "hdsp.xqua.error.can_not_delete";

    /**
     * 告警等级阈值范围重叠
     */
    String WARNING_LEVEL_OVERLAP = "hdsp.xqua.error.warning_level_overlap";

    /**
     * 该校验项仅支持返回一个值
     */
    String CHECK_ITEM_ONE_VALUE = "hdsp.xqua.error.check_item_one_value";


    /**
     * 异常阻断
     */
    String EXCEPTION_BLOCK = "hdsp.xqua.error.exception_block";

    /**
     * 校验项[{0}]未找到执行程序
     */
    String CHECK_ITEM_NOT_EXIST = "hdsp.xqua.error.measure.check_item.not_exist";
}
