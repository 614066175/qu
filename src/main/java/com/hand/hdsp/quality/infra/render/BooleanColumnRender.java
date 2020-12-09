package com.hand.hdsp.quality.infra.render;

import org.hzero.export.render.ValueRenderer;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/09 16:00
 * @since 1.0
 */
public class BooleanColumnRender implements ValueRenderer {

    @Override
    public Object render(Object value, Object data) {
        if (value instanceof Integer) {
            return ((Integer) value) == 1 ? "是" : "否";
        }
        return value;
    }

}
