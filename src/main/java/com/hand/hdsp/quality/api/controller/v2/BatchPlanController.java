package com.hand.hdsp.quality.api.controller.v2;

import com.hand.hdsp.quality.app.service.BatchPlanService;
import com.hand.hdsp.quality.config.SwaggerTags;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 批数据评估方案表 管理 API
 * 供azkaban调用
 * </p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Api(tags = SwaggerTags.BATCH_PLAN)
@RestController("batchPlanController.v2")
@RequestMapping("/v2/{organizationId}/batch-plans")
public class BatchPlanController extends BaseController {

    private final BatchPlanService batchPlanService;


    public BatchPlanController(BatchPlanService batchPlanService) {
        this.batchPlanService = batchPlanService;
    }

    @ApiOperation(value = "执行批数据评估方案")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planId",
            value = "批数据评估方案表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/exec/{planId}")
    public ResponseEntity<?> exec(@PathVariable("organizationId") Long tenantId,
                                  @PathVariable Long planId) {
        Long resultId = batchPlanService.exec(tenantId, planId);
        batchPlanService.sendMessage(planId, resultId);
        return Results.success();
    }
}
