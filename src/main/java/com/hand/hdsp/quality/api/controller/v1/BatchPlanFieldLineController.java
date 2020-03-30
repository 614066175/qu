package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.app.service.BatchPlanFieldLineService;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
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
 * <p>批数据方案-字段规则校验项表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@RestController("batchPlanFieldLineController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-field-lines")
public class BatchPlanFieldLineController extends BaseController {

    private BatchPlanFieldLineRepository batchPlanFieldLineRepository;
    private BatchPlanFieldLineService batchPlanFieldLineService;

    public BatchPlanFieldLineController(BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                        BatchPlanFieldLineService batchPlanFieldLineService) {
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.batchPlanFieldLineService = batchPlanFieldLineService;
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
                                  BatchPlanFieldLineDTO batchPlanFieldLineDTO, @ApiIgnore @SortDefault(value = BatchPlanFieldLine.FIELD_PLAN_FIELD_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanFieldLineDTO.setTenantId(tenantId);
        Page<BatchPlanFieldLineDTO> list = batchPlanFieldLineRepository.pageAndSortDTO(pageRequest, batchPlanFieldLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-可选字段规则校验项表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/option")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        return Results.success(batchPlanFieldLineService.list(batchPlanFieldDTO));
    }

    @ApiOperation(value = "批数据方案-已选字段规则校验项表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/selected")
    public ResponseEntity<?> list2(@PathVariable(name = "organizationId") Long tenantId,
                                   BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldDTO.setTenantId(tenantId);
        return Results.success(batchPlanFieldLineService.list2(batchPlanFieldDTO));
    }

    @ApiOperation(value = "批数据方案-字段规则校验项表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planFieldLineId",
            value = "批数据方案-字段规则校验项表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planFieldLineId}")
    public ResponseEntity<?> detail(@PathVariable Long planFieldLineId) {
        BatchPlanFieldLineDTO batchPlanFieldLineDTO = batchPlanFieldLineRepository.selectDTOByPrimaryKeyAndTenant(planFieldLineId);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanFieldLineDTO batchPlanFieldLineDTO) {
        batchPlanFieldLineDTO.setTenantId(tenantId);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanFieldLineDTO batchPlanFieldLineDTO) {
        batchPlanFieldLineRepository.updateDTOWhereTenant(batchPlanFieldLineDTO, tenantId);
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
