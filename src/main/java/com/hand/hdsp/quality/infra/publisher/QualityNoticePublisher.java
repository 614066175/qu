package com.hand.hdsp.quality.infra.publisher;

import org.hzero.boot.admin.bus.publisher.annotation.NoticePublisher;
import org.hzero.boot.admin.bus.publisher.annotation.NoticeTopic;

/**
 * <p>
 * 质量评估通知着
 * </p>
 *
 * @author lgl 2022/5/19 23:10
 * @since 1.0
 */
@NoticePublisher
public interface QualityNoticePublisher {

    String QUALITY_EXEC_TOPIC = "XUQA.QUALITY_EXEC";

    /**
     * 模型上线通知
     */
    @NoticeTopic(
            topic = QUALITY_EXEC_TOPIC,
            name = "质量评估",
            description = "质量评估通知")
    void sendQualityNotice(Long tenantId, Long planId, Long result);

}
