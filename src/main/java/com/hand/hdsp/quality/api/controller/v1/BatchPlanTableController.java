package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableRepository;
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
 * <p>批数据方案-表级规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@RestController("batchPlanTableController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-tables")
public class BatchPlanTableController extends BaseController {

    private BatchPlanTableRepository batchPlanTableRepository;

    public BatchPlanTableController(BatchPlanTableRepository batchPlanTableRepository) {
        this.batchPlanTableRepository = batchPlanTableRepository;
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
                                  BatchPlanTableDTO batchPlanTableDTO, @ApiIgnore @SortDefault(value = BatchPlanTable.FIELD_PLAN_TABLE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanTableDTO.setTenantId(tenantId);
        Page<BatchPlanTableDTO> list = batchPlanTableRepository.pageAndSortDTO(pageRequest, batchPlanTableDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-表级规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planTableId",
            value = "批数据方案-表级规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planTableId}")
    public ResponseEntity<?> detail(@PathVariable Long planTableId) {
        BatchPlanTableDTO batchPlanTableDTO = batchPlanTableRepository.selectDTOByPrimaryKeyAndTenant(planTableId);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        this.validObject(batchPlanTableDTO);
        batchPlanTableRepository.insertDTOSelective(batchPlanTableDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableRepository.updateDTOWhereTenant(batchPlanTableDTO, tenantId);
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
                                    @RequestBody BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableDTO.setTenantId(tenantId);
        batchPlanTableRepository.deleteByPrimaryKey(batchPlanTableDTO);
        return Results.success();
    }
}
