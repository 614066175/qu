package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.BatchPlanTableDTO;
import org.xdsp.quality.app.service.BatchPlanTableService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.BatchPlanBase;
import org.xdsp.quality.domain.entity.BatchPlanTable;
import org.xdsp.quality.domain.repository.BatchPlanTableRepository;
import org.xdsp.quality.infra.dataobject.BatchPlanTableDO;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>批数据方案-表级规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Api(tags = SwaggerTags.BATCH_PLAN_TABLE)
@RestController("batchPlanTableController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-tables")
public class BatchPlanTableController extends BaseController {

    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableService batchPlanTableService;

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
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  BatchPlanTableDTO batchPlanTableDTO, @ApiIgnore @SortDefault(value = BatchPlanTable.FIELD_PLAN_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableDTO.setProjectId(projectId);
        return Results.success(batchPlanTableService.selectTableList(pageRequest, batchPlanTableDTO));
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
    public ResponseEntity<?> listRelation(@PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          BatchPlanTableDO batchPlanTableDO) {
        batchPlanTableDO.setTenantId(tenantId);
        batchPlanTableDO.setProjectId(projectId);
        return Results.success(batchPlanTableService.list(batchPlanTableDO));
    }

    @ApiOperation(value = "批数据方案-表级规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planRuleId",
            value = "批数据方案-表级规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planRuleId}")
    public ResponseEntity<?> detail(@PathVariable Long planRuleId) {
        BatchPlanTableDTO batchPlanTableDTO = batchPlanTableService.detail(planRuleId);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableDTO.setProjectId(projectId);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableDTO.setProjectId(projectId);
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
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableDTO.setProjectId(projectId);
        batchPlanTableService.delete(batchPlanTableDTO);
        return Results.success();
    }

    @ApiOperation(value = "规则详情页面-表级规则api")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail-list")
    @ProcessLovValue(targetField = {"body", "body.warningLevelList"})
    public ResponseEntity<?> detailList(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        BatchPlanTableDTO batchPlanTableDTO, @ApiIgnore @SortDefault(value = BatchPlanBase.FIELD_PLAN_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableDTO.setProjectId(projectId);
        return Results.success(batchPlanTableService.selectDetailList(pageRequest, batchPlanTableDTO));
    }
}
