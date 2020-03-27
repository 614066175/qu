package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>批数据方案-基础配置表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@RestController("batchPlanBaseController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-bases")
public class BatchPlanBaseController extends BaseController {

    private BatchPlanBaseRepository batchPlanBaseRepository;
    private BatchPlanBaseService batchPlanBaseService;

    public BatchPlanBaseController(BatchPlanBaseRepository batchPlanBaseRepository,
                                   BatchPlanBaseService batchPlanBaseService) {
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanBaseService = batchPlanBaseService;
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
                                  BatchPlanBaseDTO batchPlanBaseDTO, @ApiIgnore @SortDefault(value = BatchPlanBase.FIELD_PLAN_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanBaseDTO.setTenantId(tenantId);
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
    public ResponseEntity<?> detail(@PathVariable Long planBaseId) {
        BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseService.detail(planBaseId);
        return Results.success(batchPlanBaseDTO);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setTenantId(tenantId);
        this.validObject(batchPlanBaseDTO);
        batchPlanBaseService.insert(batchPlanBaseDTO);
        return Results.success(batchPlanBaseDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setTenantId(tenantId);
        batchPlanBaseService.update(batchPlanBaseDTO);
        return Results.success(batchPlanBaseDTO);
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
                                    @RequestBody BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setTenantId(tenantId);
        batchPlanBaseService.delete(batchPlanBaseDTO);
        return Results.success();
    }
}
