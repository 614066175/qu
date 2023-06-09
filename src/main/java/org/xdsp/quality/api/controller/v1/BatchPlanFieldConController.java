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
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.HdspConstant;
import org.xdsp.quality.api.dto.BatchPlanFieldConDTO;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.BatchPlanFieldCon;
import org.xdsp.quality.domain.repository.BatchPlanFieldConRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>批数据方案-字段规则条件表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Api(tags = SwaggerTags.BATCH_PLAN_FIELD_CON)
@RestController("batchPlanFieldConController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-field-cons")
public class BatchPlanFieldConController extends BaseController {

    private final BatchPlanFieldConRepository batchPlanFieldConRepository;

    public BatchPlanFieldConController(BatchPlanFieldConRepository batchPlanFieldConRepository) {
        this.batchPlanFieldConRepository = batchPlanFieldConRepository;
    }

    @ApiOperation(value = "批数据方案-字段规则条件表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  BatchPlanFieldConDTO batchPlanFieldConDTO, @ApiIgnore @SortDefault(value = BatchPlanFieldCon.FIELD_CONDITION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanFieldConDTO.setTenantId(tenantId);
        batchPlanFieldConDTO.setProjectId(projectId);
        Page<BatchPlanFieldConDTO> list = batchPlanFieldConRepository.pageAndSortDTO(pageRequest, batchPlanFieldConDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-字段规则条件表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "conditionId",
            value = "批数据方案-字段规则条件表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{conditionId}")
    public ResponseEntity<?> detail(@PathVariable Long conditionId) {
        BatchPlanFieldConDTO batchPlanFieldConDTO = batchPlanFieldConRepository.selectDTOByPrimaryKeyAndTenant(conditionId);
        return Results.success(batchPlanFieldConDTO);
    }

    @ApiOperation(value = "创建批数据方案-字段规则条件表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanFieldConDTO batchPlanFieldConDTO) {
        batchPlanFieldConDTO.setTenantId(tenantId);
        batchPlanFieldConDTO.setProjectId(projectId);
        this.validObject(batchPlanFieldConDTO);
        batchPlanFieldConRepository.insertDTOSelective(batchPlanFieldConDTO);
        return Results.success(batchPlanFieldConDTO);
    }

    @ApiOperation(value = "修改批数据方案-字段规则条件表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanFieldConDTO batchPlanFieldConDTO) {
        SecurityTokenHelper.validToken(batchPlanFieldConDTO, false);
        batchPlanFieldConDTO.setProjectId(projectId);
        batchPlanFieldConRepository.updateDTOAllColumnWhereTenant(batchPlanFieldConDTO, tenantId);
        return Results.success(batchPlanFieldConDTO);
    }

    @ApiOperation(value = "删除批数据方案-字段规则条件表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanFieldConDTO batchPlanFieldConDTO) {
        batchPlanFieldConDTO.setTenantId(tenantId);
        batchPlanFieldConRepository.deleteByPrimaryKey(batchPlanFieldConDTO);
        return Results.success();
    }
}
