package org.xdsp.quality.infra.dataobject;

import lombok.*;

/**
 * <p>方案评估结果封装对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MeasureResultDO {

    private String result;

}
