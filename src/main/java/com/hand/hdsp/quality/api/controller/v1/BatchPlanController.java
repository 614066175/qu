package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.app.service.BatchPlanService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>批数据评估方案表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Api(tags = SwaggerTags.BATCH_PLAN)
@RestController("batchPlanController.v1")
@RequestMapping("/v1/{organizationId}/batch-plans")
public class BatchPlanController extends BaseController {

    private final BatchPlanRepository batchPlanRepository;
    private final BatchPlanService batchPlanService;

    public BatchPlanController(BatchPlanRepository batchPlanRepository,
                               BatchPlanService batchPlanService) {
        this.batchPlanRepository = batchPlanRepository;
        this.batchPlanService = batchPlanService;
    }

    @ApiOperation(value = "批数据评估方案表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanDTO batchPlanDTO, @ApiIgnore @SortDefault(value = BatchPlan.FIELD_PLAN_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanDTO.setTenantId(tenantId);
        Page<BatchPlanDTO> list = batchPlanRepository.pageAndSortDTO(pageRequest, batchPlanDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据评估方案表列表（不分页）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listNoPage(@PathVariable(name = "organizationId") Long tenantId,
                                        BatchPlan batchPlan) {
        batchPlan.setTenantId(tenantId);
        return Results.success(batchPlanRepository.select(batchPlan));
    }

    @ApiOperation(value = "根据方案名找到对应分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/group")
    public ResponseEntity<?> group(@PathVariable(name = "organizationId") Long tenantId,
                                   BatchPlanDTO batchPlanDTO) {
        batchPlanDTO.setTenantId(tenantId);
        return Results.success(batchPlanRepository.listByGroup(batchPlanDTO));
    }

    @ApiOperation(value = "批数据评估方案表明细")
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
    @GetMapping("/{planId}")
    public ResponseEntity<?> detail(@PathVariable Long planId) {
        BatchPlanDTO batchPlanDTO = batchPlanRepository.selectDTOByPrimaryKeyAndTenant(planId);
        return Results.success(batchPlanDTO);
    }

    @ApiOperation(value = "创建批数据评估方案表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanDTO batchPlanDTO) {
        batchPlanDTO.setTenantId(tenantId);
        this.validObject(batchPlanDTO);
        batchPlanRepository.insertDTOSelective(batchPlanDTO);
        return Results.success(batchPlanDTO);
    }

    @ApiOperation(value = "修改批数据评估方案表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanDTO batchPlanDTO) {
        batchPlanRepository.updateDTOAllColumnWhereTenant(batchPlanDTO, tenantId);
        return Results.success(batchPlanDTO);
    }

    @ApiOperation(value = "删除批数据评估方案表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanDTO batchPlanDTO) {
        batchPlanDTO.setTenantId(tenantId);
        batchPlanService.delete(batchPlanDTO);
        return Results.success();
    }

    @ApiOperation(value = "生成数据质量任务")
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
    @GetMapping("/generate/{planId}")
    public ResponseEntity<?> generate(@PathVariable("organizationId") Long tenantId,
                                      @PathVariable Long planId) {
        batchPlanService.generate(tenantId, planId);
        return Results.success();
    }

}
