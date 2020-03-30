package com.hand.hdsp.quality.infra.measure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * <p>校验项处理</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class CheckItemProcessor implements BeanPostProcessor {

    @Autowired
    private MeasureCollector measureCollector;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        CheckItem type = clazz.getAnnotation(CheckItem.class);
        if (type == null || !(bean instanceof Measure)) {
            return bean;
        }
        String value = type.value();
        measureCollector.register(value.toUpperCase(), (Measure) bean);
        return bean;
    }

}
