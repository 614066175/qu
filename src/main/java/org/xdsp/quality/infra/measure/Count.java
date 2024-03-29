package org.xdsp.quality.infra.measure;

import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;

/**
 * <p>校验类型抽象类：负责处理不同类型的计算逻辑</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface Count {
    /**
     * 校验
     *
     * @param param 参数封装对象
     * @return
     */
    BatchResultItem count(MeasureParamDO param);
}
