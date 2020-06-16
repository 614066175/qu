package com.hand.hdsp.quality.infra.measure;

import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;

/**
 * <p>测量抽象类：负责执行统计任务，生成统计结果</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface Measure {

    /**
     * 校验
     *
     * @param param 参数封装对象
     * @return
     */
    BatchResultItem check(MeasureParamDO param);
}
