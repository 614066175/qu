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
import org.xdsp.quality.api.dto.BatchPlanFieldDTO;
import org.xdsp.quality.app.service.BatchPlanFieldService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.BatchPlanBase;
import org.xdsp.quality.domain.entity.BatchPlanField;
import org.xdsp.quality.domain.repository.BatchPlanFieldRepository;
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

    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldService batchPlanFieldService;

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
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  BatchPlanField batchPlanField) {
        batchPlanField.setTenantId(tenantId);
        batchPlanField.setProjectId(projectId);
        return Results.success(batchPlanFieldRepository.select(batchPlanField));
    }

    @ApiOperation(value = "批数据方案-已选字段规则列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list-selected")
    public ResponseEntity<?> listSelected(@PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        batchPlanFieldDTO.setProjectId(projectId);
        return Results.success(batchPlanFieldService.listSelected(batchPlanFieldDTO));
    }

    @ApiOperation(value = "批数据方案-可选标准规则详情（将标准规则转成字段规则的结构）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "ruleId",
            value = "规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/select-detail/{ruleId}")
    public ResponseEntity<?> selectDetail(@PathVariable Long ruleId) {
        return Results.success(batchPlanFieldService.selectDetail(ruleId));
    }

    @ApiOperation(value = "批数据方案-字段规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planRuleId",
            value = "批数据方案-字段规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planRuleId}")
    public ResponseEntity<?> detail(@PathVariable Long planRuleId) {
        BatchPlanFieldDTO batchPlanFieldDTO = batchPlanFieldService.detail(planRuleId);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        batchPlanFieldDTO.setProjectId(projectId);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        batchPlanFieldDTO.setProjectId(projectId);
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
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        batchPlanFieldDTO.setProjectId(projectId);
        batchPlanFieldService.delete(batchPlanFieldDTO);
        return Results.success();
    }

    @ApiOperation(value = "规则详情页面-字段规则api")
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
                                        BatchPlanFieldDTO batchPlanFieldDTO, @ApiIgnore @SortDefault(value = BatchPlanBase.FIELD_PLAN_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanFieldDTO.setTenantId(tenantId);
        batchPlanFieldDTO.setProjectId(projectId);
        return Results.success(batchPlanFieldService.selectDetailList(pageRequest, batchPlanFieldDTO));
    }
}
