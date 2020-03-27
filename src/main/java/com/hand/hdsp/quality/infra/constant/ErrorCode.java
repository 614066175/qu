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
     * 分组下存在规则或其他分组，或方案下存在行信息，无法删除
     */
    String CAN_NOT_DELETE ="error.can_not_delete";
}
