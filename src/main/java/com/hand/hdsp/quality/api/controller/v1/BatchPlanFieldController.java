package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.app.service.BatchPlanFieldService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
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
 * <p>批数据方案-字段规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Api(tags = SwaggerTags.BATCH_PLAN_FIELD)
@RestController("batchPlanFieldController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-fields")
public class BatchPlanFieldController extends BaseController {

    private BatchPlanFieldRepository batchPlanFieldRepository;
    private BatchPlanFieldService batchPlanFieldService;

    public BatchPlanFieldController(BatchPlanFieldRepository batchPlanFieldRepository,
                                    BatchPlanFieldService batchPlanFieldService) {
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldService = batchPlanFieldService;
    }

    @ApiOperation(value = "批数据方案-字段规则表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanField batchPlanField) {
        batchPlanField.setTenantId(tenantId);
        return Results.success(batchPlanFieldRepository.select(batchPlanField));
    }

    @ApiOperation(value = "批数据方案-字段规则表列表（含已选规则）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanFieldDTO batchPlanFieldDTO, @ApiIgnore @SortDefault(value = BatchPlanField.FIELD_PLAN_FIELD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanFieldDTO.setTenantId(tenantId);
        return Results.success(batchPlanFieldService.list(pageRequest, batchPlanFieldDTO));
    }

    @ApiOperation(value = "批数据方案-字段规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planFieldId",
            value = "批数据方案-字段规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planFieldId}")
    public ResponseEntity<?> detail(@PathVariable Long planFieldId) {
        BatchPlanFieldDTO batchPlanFieldDTO = batchPlanFieldService.detail(planFieldId);
        return Results.success(batchPlanFieldDTO);
    }

    @ApiOperation(value = "创建批数据方案-字段规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        this.validObject(batchPlanFieldDTO);
        batchPlanFieldService.insert(batchPlanFieldDTO);
        return Results.success(batchPlanFieldDTO);
    }

    @ApiOperation(value = "修改批数据方案-字段规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        batchPlanFieldService.update(batchPlanFieldDTO);
        return Results.success(batchPlanFieldDTO);
    }

    @ApiOperation(value = "删除批数据方案-字段规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        batchPlanFieldService.delete(batchPlanFieldDTO);
        return Results.success();
    }
}
