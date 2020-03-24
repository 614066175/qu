package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableLineRepository;
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
 * <p>批数据方案-表级规则校验项表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@RestController("batchPlanTableLineController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-table-lines")
public class BatchPlanTableLineController extends BaseController {

    private BatchPlanTableLineRepository batchPlanTableLineRepository;

    public BatchPlanTableLineController(BatchPlanTableLineRepository batchPlanTableLineRepository) {
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
    }

    @ApiOperation(value = "批数据方案-表级规则校验项表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanTableLineDTO batchPlanTableLineDTO, @ApiIgnore @SortDefault(value = BatchPlanTableLine.FIELD_PLAN_TABLE_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanTableLineDTO.setTenantId(tenantId);
        Page<BatchPlanTableLineDTO> list = batchPlanTableLineRepository.pageAndSortDTO(pageRequest, batchPlanTableLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-表级规则校验项表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planTableLineId",
            value = "批数据方案-表级规则校验项表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planTableLineId}")
    public ResponseEntity<?> detail(@PathVariable Long planTableLineId) {
        BatchPlanTableLineDTO batchPlanTableLineDTO = batchPlanTableLineRepository.selectDTOByPrimaryKeyAndTenant(planTableLineId);
        return Results.success(batchPlanTableLineDTO);
    }

    @ApiOperation(value = "创建批数据方案-表级规则校验项表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanTableLineDTO batchPlanTableLineDTO) {
        batchPlanTableLineDTO.setTenantId(tenantId);
        this.validObject(batchPlanTableLineDTO);
        batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);
        return Results.success(batchPlanTableLineDTO);
    }

    @ApiOperation(value = "修改批数据方案-表级规则校验项表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanTableLineDTO batchPlanTableLineDTO) {
        batchPlanTableLineRepository.updateDTOWhereTenant(batchPlanTableLineDTO, tenantId);
        return Results.success(batchPlanTableLineDTO);
    }

    @ApiOperation(value = "删除批数据方案-表级规则校验项表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanTableLineDTO batchPlanTableLineDTO) {
        batchPlanTableLineDTO.setTenantId(tenantId);
        batchPlanTableLineRepository.deleteByPrimaryKey(batchPlanTableLineDTO);
        return Results.success();
    }
}
