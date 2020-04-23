package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableLineDTO;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableLineRepository;
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
 * <p>批数据方案-表间规则关联关系表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Api(tags = SwaggerTags.BATCH_PLAN_REL_TABLE_LINE)
@RestController("batchPlanRelTableLineController.v1")
@RequestMapping("/v1/{organizationId}/batch-plan-rel-table-lines")
public class BatchPlanRelTableLineController extends BaseController {

    private BatchPlanRelTableLineRepository batchPlanRelTableLineRepository;

    public BatchPlanRelTableLineController(BatchPlanRelTableLineRepository batchPlanRelTableLineRepository) {
        this.batchPlanRelTableLineRepository = batchPlanRelTableLineRepository;
    }

    @ApiOperation(value = "批数据方案-表间规则关联关系表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchPlanRelTableLineDTO batchPlanRelTableLineDTO, @ApiIgnore @SortDefault(value = BatchPlanRelTableLine.FIELD_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchPlanRelTableLineDTO.setTenantId(tenantId);
        Page<BatchPlanRelTableLineDTO> list = batchPlanRelTableLineRepository.pageAndSortDTO(pageRequest, batchPlanRelTableLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-表间规则关联关系表列表（不分页）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                     BatchPlanRelTableLine batchPlanRelTableLine) {
        batchPlanRelTableLine.setTenantId(tenantId);
        List<BatchPlanRelTableLine> list = batchPlanRelTableLineRepository.select(batchPlanRelTableLine);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案-表间规则关联关系表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "lineId",
            value = "批数据方案-表间规则关联关系表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{lineId}")
    public ResponseEntity<?> detail(@PathVariable Long lineId) {
        BatchPlanRelTableLineDTO batchPlanRelTableLineDTO = batchPlanRelTableLineRepository.selectDTOByPrimaryKeyAndTenant(lineId);
        return Results.success(batchPlanRelTableLineDTO);
    }

    @ApiOperation(value = "创建批数据方案-表间规则关联关系表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanRelTableLineDTO batchPlanRelTableLineDTO) {
        batchPlanRelTableLineDTO.setTenantId(tenantId);
        this.validObject(batchPlanRelTableLineDTO);
        batchPlanRelTableLineRepository.insertDTOSelective(batchPlanRelTableLineDTO);
        return Results.success(batchPlanRelTableLineDTO);
    }

    @ApiOperation(value = "修改批数据方案-表间规则关联关系表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchPlanRelTableLineDTO batchPlanRelTableLineDTO) {
        batchPlanRelTableLineRepository.updateDTOWhereTenant(batchPlanRelTableLineDTO, tenantId);
        return Results.success(batchPlanRelTableLineDTO);
    }

    @ApiOperation(value = "删除批数据方案-表间规则关联关系表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchPlanRelTableLineDTO batchPlanRelTableLineDTO) {
        batchPlanRelTableLineDTO.setTenantId(tenantId);
        batchPlanRelTableLineRepository.deleteByPrimaryKey(batchPlanRelTableLineDTO);
        return Results.success();
    }
}
