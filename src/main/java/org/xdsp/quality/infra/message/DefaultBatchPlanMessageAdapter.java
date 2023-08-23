package org.xdsp.quality.infra.message;

import org.hzero.boot.alert.vo.InboundMessage;
import org.springframework.stereotype.Component;
import org.xdsp.quality.message.adapter.BatchPlanMessageAdapter;

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
