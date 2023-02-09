package com.hand.hdsp.quality.infra.export;

/**
 * @Title: Exporter
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 14:00
 */
public interface Exporter<T, R> {
    /**
     * 导出接口
     * @param t
     * @return
     */
    R export(T t);
}
