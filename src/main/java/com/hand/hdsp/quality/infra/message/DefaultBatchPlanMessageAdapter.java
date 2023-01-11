package com.hand.hdsp.quality.infra.message;

import com.hand.hdsp.message.common.infra.quality.BatchPlanMessageAdapter;
import org.springframework.stereotype.Component;

import org.hzero.boot.alert.vo.InboundMessage;
import org.hzero.boot.message.entity.MessageSender;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/11 20:13
 */
@Component
public class DefaultBatchPlanMessageAdapter implements BatchPlanMessageAdapter<InboundMessage,Void> {

    @Override
    public Void sendMessage(InboundMessage inboundMessage) {
        return null;
    }
}
