package com.hand.hdsp.quality.infra.constant;

/**
 * <p>
 * 质量工单常量
 * </p>
 *
 * @author lgl 2022/4/20 20:23
 * @since 1.0
 */
public interface WorkOrderConstants {

    /**
     * 工单状态
     */
    interface WorkOrderStatus {
        //待接收
        String PENDING_RECEIVE = "PENDING_RECEIVE";

        //处理中
        String PROCESSING = "PROCESSING";

        //已接收
        String RECEIVED = "RECEIVED";

    }

    /**
     * 工单操作类型
     */
    interface OrderOperateType {

        //发起
        String LAUNCH = "LAUNCH";

        //接收
        String RECEIVE = "RECEIVE";

        //处理
        String PROCESS = "PROCESS";
    }
}
