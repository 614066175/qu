package org.xdsp.quality.api.controller.v1;

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
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.BatchPlanFieldLineDTO;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.BatchPlanFieldLine;
import org.xdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>批数据方案-字段规则校验项表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Api(tags = SwaggerTags.BATCH_PLAN_FIELD_LINE)
@RestController("batchPlanFieldLineController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-field-lines")
public class BatchPlanFieldLineController extends BaseController {

    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;

    public BatchPlanFieldLineController(BatchPlanFieldLineRepository batchPlanFieldLineRepository) {
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
    }

    @ApiOperation(value = "批数据方案-字段规则校验项表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  BatchPlanFieldLineDTO batchPlanFieldLineDTO, @ApiIgnore @SortDefault(value = BatchPlanFieldLine.FIELD_PLAN_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanFieldLineDTO.setTenantId(tenantId);
        batchPlanFieldLineDTO.setProjectId(projectId);
        Page<BatchPlanFieldLineDTO> list = batchPlanFieldLineRepository.pageAndSortDTO(pageRequest, batchPlanFieldLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-字段规则校验项表列表（不分页）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                     BatchPlanFieldLine batchPlanFieldLine) {
        batchPlanFieldLine.setTenantId(tenantId);
        batchPlanFieldLine.setProjectId(projectId);
        List<BatchPlanFieldLine> list = batchPlanFieldLineRepository.select(batchPlanFieldLine);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-字段规则校验项表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planLineId",
            value = "批数据方案-字段规则校验项表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planLineId}")
    public ResponseEntity<?> detail(@PathVariable Long planLineId) {
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = batchPlanFieldLineRepository.selectDTOByPrimaryKeyAndTenant(planLineId);
        return Results.success(batchPlanFieldLineDTO);
    }

    @ApiOperation(value = "创建批数据方案-字段规则校验项表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanFieldLineDTO batchPlanFieldLineDTO) {
        batchPlanFieldLineDTO.setTenantId(tenantId);
        batchPlanFieldLineDTO.setProjectId(projectId);
        this.validObject(batchPlanFieldLineDTO);
        batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
        return Results.success(batchPlanFieldLineDTO);
    }

    @ApiOperation(value = "修改批数据方案-字段规则校验项表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanFieldLineDTO batchPlanFieldLineDTO) {
        batchPlanFieldLineDTO.setProjectId(projectId);
        batchPlanFieldLineRepository.updateDTOAllColumnWhereTenant(batchPlanFieldLineDTO, tenantId);
        return Results.success(batchPlanFieldLineDTO);
    }

    @ApiOperation(value = "删除批数据方案-字段规则校验项表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanFieldLineDTO batchPlanFieldLineDTO) {
        batchPlanFieldLineDTO.setTenantId(tenantId);
        batchPlanFieldLineRepository.deleteByPrimaryKey(batchPlanFieldLineDTO);
        return Results.success();
    }
}
