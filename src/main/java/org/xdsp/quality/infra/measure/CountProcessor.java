package org.xdsp.quality.infra.measure;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * <p>校验项处理</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class CountProcessor implements BeanPostProcessor {

    private final CountCollector countCollector;

    public CountProcessor(CountCollector countCollector) {
        this.countCollector = countCollector;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        //校验类型
        CountType countType = clazz.getAnnotation(CountType.class);
        if (countType == null || !(bean instanceof Count)) {
            return bean;
        }
        String countTypeValue = countType.value();
        countCollector.register(countTypeValue.toUpperCase(), (Count) bean);
        return bean;
    }

}
