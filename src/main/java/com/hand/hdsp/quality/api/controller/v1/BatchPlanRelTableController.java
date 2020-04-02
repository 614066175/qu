package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.app.service.BatchPlanRelTableService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableRepository;
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
 * <p>批数据方案-表间规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Api(tags = SwaggerTags.BATCH_PLAN_REL_TABLE)
@RestController("batchPlanRelTableController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-rel-tables")
public class BatchPlanRelTableController extends BaseController {

    private BatchPlanRelTableRepository batchPlanRelTableRepository;
    private BatchPlanRelTableService batchPlanRelTableService;

    public BatchPlanRelTableController(BatchPlanRelTableRepository batchPlanRelTableRepository,
                                       BatchPlanRelTableService batchPlanRelTableService) {
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableService = batchPlanRelTableService;
    }

    @ApiOperation(value = "批数据方案-表间规则表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanRelTableDTO batchPlanRelTableDTO, @ApiIgnore @SortDefault(value = BatchPlanRelTable.FIELD_PLAN_REL_TABLE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        Page<BatchPlanRelTableDTO> list = batchPlanRelTableRepository.pageAndSortDTO(pageRequest, batchPlanRelTableDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-表间规则表列表（不分页）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanRelTable batchPlanRelTable) {
        batchPlanRelTable.setTenantId(tenantId);
        return Results.success(batchPlanRelTableRepository.select(batchPlanRelTable));
    }

    @ApiOperation(value = "批数据方案-表间规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planRelTableId",
            value = "批数据方案-表间规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planRelTableId}")
    public ResponseEntity<?> detail(@PathVariable Long planRelTableId) {
        BatchPlanRelTableDTO batchPlanRelTableDTO = batchPlanRelTableService.detail(planRelTableId);
        return Results.success(batchPlanRelTableDTO);
    }

    @ApiOperation(value = "创建批数据方案-表间规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanRelTableDTO batchPlanRelTableDTO) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        this.validObject(batchPlanRelTableDTO);
        batchPlanRelTableService.insert(batchPlanRelTableDTO);
        return Results.success(batchPlanRelTableDTO);
    }

    @ApiOperation(value = "修改批数据方案-表间规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanRelTableDTO batchPlanRelTableDTO) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        batchPlanRelTableService.update(batchPlanRelTableDTO);
        return Results.success(batchPlanRelTableDTO);
    }

    @ApiOperation(value = "删除批数据方案-表间规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanRelTableDTO batchPlanRelTableDTO) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        batchPlanRelTableService.delete(batchPlanRelTableDTO);
        return Results.success();
    }
}
