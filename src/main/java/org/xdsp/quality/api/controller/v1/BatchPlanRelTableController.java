package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
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
import org.xdsp.quality.api.dto.BatchPlanRelTableDTO;
import org.xdsp.quality.app.service.BatchPlanRelTableService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.BatchPlanRelTable;
import org.xdsp.quality.domain.repository.BatchPlanRelTableRepository;
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

    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchPlanRelTableService batchPlanRelTableService;

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
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  BatchPlanRelTableDTO batchPlanRelTableDTO, @ApiIgnore @SortDefault(value = BatchPlanRelTable.FIELD_PLAN_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        batchPlanRelTableDTO.setProjectId(projectId);
        Page<BatchPlanRelTableDTO> list = batchPlanRelTableService.list(pageRequest, batchPlanRelTableDTO);
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
    public ResponseEntity<?> listNoPage(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        BatchPlanRelTable batchPlanRelTable) {
        batchPlanRelTable.setTenantId(tenantId);
        batchPlanRelTable.setProjectId(projectId);
        return Results.success(batchPlanRelTableRepository.select(batchPlanRelTable));
    }

    @ApiOperation(value = "批数据方案-表间规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planRuleId",
            value = "批数据方案-表间规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planRuleId}")
    public ResponseEntity<?> detail(@PathVariable Long planRuleId) {
        BatchPlanRelTableDTO batchPlanRelTableDTO = batchPlanRelTableService.detail(planRuleId);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanRelTableDTO batchPlanRelTableDTO) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        batchPlanRelTableDTO.setProjectId(projectId);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanRelTableDTO batchPlanRelTableDTO) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        batchPlanRelTableDTO.setProjectId(projectId);
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
        batchPlanRelTableRepository.deleteByPrimaryKey(batchPlanRelTableDTO);
        return Results.success();
    }

    @ApiOperation(value = "规则详情页面-表间规则api")
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
                                        BatchPlanRelTableDTO batchPlanRelTableDTO, @ApiIgnore @SortDefault(value = BatchPlanRelTable.FIELD_PLAN_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanRelTableDTO.setTenantId(tenantId);
        batchPlanRelTableDTO.setProjectId(projectId);
        Page<BatchPlanRelTableDTO> list = batchPlanRelTableService.selectDetailList(pageRequest, batchPlanRelTableDTO);
        return Results.success(list);
    }
}
