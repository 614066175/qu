package com.hand.hdsp.quality.infra.dataobject;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import lombok.*;

/**
 * <p>方案评估参数封装对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MeasureParamDO {

    private Long tenantId;

    private DatasourceDTO datasourceDTO;

    private BatchPlanTableDTO batchPlanTableDTO;

    private BatchPlanTableLineDTO batchPlanTableLineDTO;

    private BatchPlanField batchPlanField;

    private BatchPlanFieldLineDTO batchPlanFieldLineDTO;

    private BatchPlanRelTable batchPlanRelTable;

    private BatchResultBase batchResultBase;
}
