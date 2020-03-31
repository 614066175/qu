package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.app.service.BatchPlanTableService;
import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableRepository;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>批数据方案-表级规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@RestController("batchPlanTableController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-tables")
public class BatchPlanTableController extends BaseController {

    private BatchPlanTableRepository batchPlanTableRepository;
    private BatchPlanTableService batchPlanTableService;

    public BatchPlanTableController(BatchPlanTableRepository batchPlanTableRepository,
                                    BatchPlanTableService batchPlanTableService) {
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableService = batchPlanTableService;
    }

    @ApiOperation(value = "批数据方案-表级规则表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanTable batchPlanTable) {
        batchPlanTable.setTenantId(tenantId);
        return Results.success(batchPlanTableRepository.select(batchPlanTable));
    }

    @ApiOperation(value = "批数据方案-表级规则表列表（含校验项）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanTableDO batchPlanTableDO) {
        batchPlanTableDO.setTenantId(tenantId);
        return Results.success(batchPlanTableService.list(batchPlanTableDO));
    }

    @ApiOperation(value = "批数据方案-表级规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planTableId",
            value = "批数据方案-表级规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planTableId}")
    public ResponseEntity<?> detail(@PathVariable Long planTableId) {
        BatchPlanTableDTO batchPlanTableDTO = batchPlanTableService.detail(planTableId);
        return Results.success(batchPlanTableDTO);
    }

    @ApiOperation(value = "创建批数据方案-表级规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        this.validObject(batchPlanTableDTO);
        batchPlanTableService.insert(batchPlanTableDTO);
        return Results.success(batchPlanTableDTO);
    }

    @ApiOperation(value = "修改批数据方案-表级规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableService.update(batchPlanTableDTO);
        return Results.success(batchPlanTableDTO);
    }

    @ApiOperation(value = "删除批数据方案-表级规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableService.delete(batchPlanTableDTO);
        return Results.success();
    }
}
