package com.hand.hdsp.quality.infra.listener;

import com.hand.hdsp.quality.infra.util.AnsjUtil;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.admin.bus.listener.annotation.NoticeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2022/7/25 14:22
 * @since 1.0
 */
@Component
@Slf4j
public class DicChangeListener {

    @Autowired
    private AnsjUtil ansjUtil;

    public static final String DIC_CHANGE_TOPIC = "XSTA.DIC_CHANGE";


    @NoticeListener(topic = DIC_CHANGE_TOPIC)
    public void sendDicChangeNotice(String fileName) {
        ansjUtil.deleteDicFile(fileName);
    }
}
