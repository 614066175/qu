package com.hand.hdsp.quality.infra.util;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.util.List;

/**
 * 描述：
 *
 * @version 1.0.0
 * @author: 邓志龙 zhilong.deng@hand-china.com
 * @data: 20-7-27 上午10:36
 */
@Component
@Slf4j
public class EurekaUtil {
    private final EurekaClient eurekaClient;

    public EurekaUtil(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    public InstanceInfo getAppByName(String appName) {
        List<InstanceInfo> instances = eurekaClient.getApplication(appName).getInstances();
        if (CollectionUtils.isEmpty(instances)) {
            throw new CommonException("无法找到对应的app:{}", appName);
        }
        for (InstanceInfo instance : instances) {
            try {
                if (InetAddress.getByName(instance.getHostName()).isReachable(100) || InetAddress.getByName(instance.getIPAddr()).isReachable(100)) {
                    return instance;
                }
            } catch (Exception e) {
                log.error("{}网络无法ping通", instance.getId(), e);
            }
        }
        return instances.get(0);
    }
}
