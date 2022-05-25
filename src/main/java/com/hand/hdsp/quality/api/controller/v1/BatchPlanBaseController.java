package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.ColumnDTO;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
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

import java.util.List;

/**
 * <p>批数据方案-基础配置表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Api(tags = SwaggerTags.BATCH_PLAN_BASE)
@RestController("batchPlanBaseController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-bases")
public class BatchPlanBaseController extends BaseController {

    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final BatchPlanBaseService batchPlanBaseService;

    public BatchPlanBaseController(BatchPlanBaseRepository batchPlanBaseRepository,
                                   BatchPlanBaseService batchPlanBaseService) {
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanBaseService = batchPlanBaseService;
    }

    @ApiOperation(value = "批数据方案-基础配置表列表（含规则计数）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/list")
    public ResponseEntity<?> listBase(@PathVariable(name = "organizationId") Long tenantId,
                                      @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                      @RequestBody BatchPlanBaseDTO batchPlanBaseDTO, @ApiIgnore @SortDefault(value = BatchPlanBase.FIELD_PLAN_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanBaseDTO.setTenantId(tenantId);
        batchPlanBaseDTO.setProjectId(projectId);
        Page<BatchPlanBaseDTO> list = batchPlanBaseRepository.list(pageRequest, batchPlanBaseDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-基础配置表列表")
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
                                  BatchPlanBaseDTO batchPlanBaseDTO, @ApiIgnore @SortDefault(value = BatchPlanBase.FIELD_PLAN_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanBaseDTO.setTenantId(tenantId);
        batchPlanBaseDTO.setProjectId(projectId);
        Page<BatchPlanBaseDTO> list = batchPlanBaseRepository.pageAndSortDTO(pageRequest, batchPlanBaseDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-基础配置表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planBaseId",
            value = "批数据方案-基础配置表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planBaseId}")
    public ResponseEntity<?> detail(@PathVariable(name = "organizationId") Long tenantId,
                                    @PathVariable Long planBaseId,
                                    @RequestParam Long currentPlanId) {
        return Results.success(batchPlanBaseService.detail(planBaseId, currentPlanId, tenantId));
    }

    @ApiOperation(value = "创建批数据方案-基础配置表")
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
                                    @RequestBody BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setTenantId(tenantId);
        batchPlanBaseDTO.setProjectId(projectId);
        this.validObject(batchPlanBaseDTO);
        return Results.success(batchPlanBaseService.create(batchPlanBaseDTO));
    }

    @ApiOperation(value = "修改批数据方案-基础配置表")
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
                                    @RequestBody BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setProjectId(projectId);
        batchPlanBaseDTO.setTenantId(tenantId);
        return Results.success(batchPlanBaseService.update(batchPlanBaseDTO));
    }

    @ApiOperation(value = "删除批数据方案-基础配置表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setTenantId(tenantId);
        batchPlanBaseDTO.setProjectId(projectId);
        batchPlanBaseService.delete(batchPlanBaseDTO);
        return Results.success();
    }

    @ApiOperation(value = "移除（取消分配）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/cancel-assign")
    public ResponseEntity<?> cancelAssign(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          @RequestBody BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setTenantId(tenantId);
        batchPlanBaseDTO.setProjectId(projectId);
        batchPlanBaseService.cancelAssign(batchPlanBaseDTO);
        return Results.success(batchPlanBaseDTO);
    }

    @ApiOperation(value = "解析自定义SQL字段信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/columns")
    public ResponseEntity<List<ColumnDTO>> columns(@PathVariable("organizationId") Long tenantId,
                                                   @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                   @RequestBody ColumnDTO columnDTO) {
        List<ColumnDTO> tables = batchPlanBaseService.columns(columnDTO.getSql());
        return Results.success(tables);
    }
}
