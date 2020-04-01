package com.hand.hdsp.quality.infra.dataobject;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import lombok.*;

import java.util.List;

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

    private BatchPlanFieldDTO batchPlanFieldDTO;

    private BatchPlanFieldLineDTO batchPlanFieldLineDTO;

    private BatchPlanRelTable batchPlanRelTable;

    private List<BatchPlanRelTableLine> batchPlanRelTableLineList;

    private List<PlanWarningLevel> warningLevelList;

    private BatchResultBase batchResultBase;
}
