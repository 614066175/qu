package org.xdsp.quality.api.controller.v2;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.app.service.BatchPlanService;
import org.xdsp.quality.config.SwaggerTags;

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

    /**
     * 不走v2
     *
     * @param tenantId
     * @param planId
     * @return
     */
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
    @Deprecated
    public ResponseEntity<?> exec(@PathVariable("organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  @PathVariable Long planId) {
        Long resultId = batchPlanService.exec(tenantId, planId, projectId);
        batchPlanService.sendMessage(planId, resultId);
        return Results.success();
    }
}
