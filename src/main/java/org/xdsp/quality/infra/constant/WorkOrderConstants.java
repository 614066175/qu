package org.xdsp.quality.infra.constant;

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

        //已拒绝
        String REFUSED = "REFUSED";

        //已解决
        String PROCESSED = "PROCESSED";

    }

    /**
     * 工单操作类型
     */
    interface OrderOperateType {

        //下发
        String LAUNCH = "LAUNCH";

        //接收
        String RECEIVE = "RECEIVE";

        //开始处理
        String START_PROCESS = "START_PROCESS";

        //拒绝
        String REFUSE = "REFUSE";

        //转交
        String ASSIGN = "ASSIGN";

        //提交解决方案
        String SUBMIT_SOLUTION = "SUBMIT_SOLUTION";


    }
}
