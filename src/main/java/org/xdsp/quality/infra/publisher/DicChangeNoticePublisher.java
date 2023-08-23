package org.xdsp.quality.infra.publisher;

import org.hzero.boot.admin.bus.publisher.annotation.NoticePublisher;
import org.hzero.boot.admin.bus.publisher.annotation.NoticeTopic;

/**
 * @Title: DicChangeNoticePublisher
 * @Description: 词库变化通知
 * @author: lgl
 * @date: 2022/12/6 21:55
 */
@NoticePublisher
public interface DicChangeNoticePublisher {

    String DIC_CHANGE_TOPIC = "XSTA.DIC_CHANGE";

    /**
     * 词库变更通知
     */
    @NoticeTopic(
            topic = DIC_CHANGE_TOPIC,
            name = "词库变更通知",
            description = "词库变更通知")
    void sendDicChangeNotice(String fileName);
}
