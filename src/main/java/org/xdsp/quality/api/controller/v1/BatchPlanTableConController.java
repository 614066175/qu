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
import org.xdsp.quality.api.dto.BatchPlanTableConDTO;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.BatchPlanTableCon;
import org.xdsp.quality.domain.repository.BatchPlanTableConRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>批数据方案-表级规则条件表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Api(tags = SwaggerTags.BATCH_PLAN_TABLE_CON)
@RestController("batchPlanTableConController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-table-cons")
public class BatchPlanTableConController extends BaseController {

    private final BatchPlanTableConRepository batchPlanTableConRepository;

    public BatchPlanTableConController(BatchPlanTableConRepository batchPlanTableConRepository) {
        this.batchPlanTableConRepository = batchPlanTableConRepository;
    }

    @ApiOperation(value = "批数据方案-表级规则条件表列表")
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
                                  BatchPlanTableConDTO batchPlanTableConDTO, @ApiIgnore @SortDefault(value = BatchPlanTableCon.FIELD_CONDITION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanTableConDTO.setTenantId(tenantId);
        batchPlanTableConDTO.setProjectId(projectId);
        Page<BatchPlanTableConDTO> list = batchPlanTableConRepository.pageAndSortDTO(pageRequest, batchPlanTableConDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-表级规则条件表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "conditionId",
            value = "批数据方案-表级规则条件表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{conditionId}")
    public ResponseEntity<?> detail(@PathVariable Long conditionId) {
        BatchPlanTableConDTO batchPlanTableConDTO = batchPlanTableConRepository.selectDTOByPrimaryKeyAndTenant(conditionId);
        return Results.success(batchPlanTableConDTO);
    }

    @ApiOperation(value = "创建批数据方案-表级规则条件表")
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
                                    @RequestBody BatchPlanTableConDTO batchPlanTableConDTO) {
        batchPlanTableConDTO.setTenantId(tenantId);
        batchPlanTableConDTO.setProjectId(projectId);
        this.validObject(batchPlanTableConDTO);
        batchPlanTableConRepository.insertDTOSelective(batchPlanTableConDTO);
        return Results.success(batchPlanTableConDTO);
    }

    @ApiOperation(value = "修改批数据方案-表级规则条件表")
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
                                    @RequestBody BatchPlanTableConDTO batchPlanTableConDTO) {
        batchPlanTableConDTO.setProjectId(projectId);
        batchPlanTableConRepository.updateDTOAllColumnWhereTenant(batchPlanTableConDTO, tenantId);
        return Results.success(batchPlanTableConDTO);
    }

    @ApiOperation(value = "删除批数据方案-表级规则条件表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanTableConDTO batchPlanTableConDTO) {
        batchPlanTableConDTO.setTenantId(tenantId);
        batchPlanTableConRepository.deleteByPrimaryKey(batchPlanTableConDTO);
        return Results.success();
    }
}
