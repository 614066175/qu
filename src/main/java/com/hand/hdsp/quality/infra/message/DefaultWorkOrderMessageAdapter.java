package com.hand.hdsp.quality.infra.message;

import com.hand.hdsp.message.common.infra.quality.WorkOrderMessageAdapter;
import org.springframework.stereotype.Component;

import org.hzero.boot.message.entity.MessageSender;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/01/11 20:05
 */
@Component
public class DefaultWorkOrderMessageAdapter implements WorkOrderMessageAdapter<MessageSender,Void> {

    @Override
    public Void sendMessage(MessageSender messageSender) {
        return null;
    }
}
