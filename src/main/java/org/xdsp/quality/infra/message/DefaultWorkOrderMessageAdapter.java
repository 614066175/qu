package org.xdsp.quality.infra.message;

import org.hzero.boot.message.entity.MessageSender;
import org.springframework.stereotype.Component;
import org.xdsp.quality.message.adapter.WorkOrderMessageAdapter;

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
