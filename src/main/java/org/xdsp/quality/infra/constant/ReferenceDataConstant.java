package org.xdsp.quality.infra.constant;

/**
 * 参考数据常量
 * @author fuqiang.luo@hanc-china.com
 */
public interface ReferenceDataConstant {
    int RELEASE = 1;
    int OFFLINE = 0;
    int RUNNING = 0;
    int PASS = 1;
    int REJECT = 2;
    int WITHDRAW = 3;


    String RELEASE_WORKFLOW = "FLOW1648155295382052866";

    String OFFLINE_WORKFLOW = "FLOW1648143827911647233";

    String EXTERNAL_RELEASE_WORKFLOW = "FLOW1753984754258116610";

    String EXTERNAL_OFFLINE_WORKFLOW = "FLOW1753984974282915841";

    String RECORD_ID = "recordId";

    String DATA_ID = "dataId";

    String RESPONSIBLE_PERSON = "responsiblePerson";

    String NEW = "NEW";

    String RELEASE_ING = "RELEASE_ING";

    String RELEASED = "RELEASED";

    String OFFLINE_ING = "OFFLINE_ING";

    String OFFLINE_ = "OFFLINE";
}
