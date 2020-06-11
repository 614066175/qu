package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.repository.BatchResultItemRepository;
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
 * <p>批数据方案结果表-校验项信息 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@RestController("batchResultItemController.v1")
@RequestMapping("/v1/{organizationId}/batch-result-items")
public class BatchResultItemController extends BaseController {

    private BatchResultItemRepository batchResultItemRepository;

    public BatchResultItemController(BatchResultItemRepository batchResultItemRepository) {
        this.batchResultItemRepository = batchResultItemRepository;
    }

    @ApiOperation(value = "批数据方案结果表-校验项信息列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchResultItemDTO batchResultItemDTO, @ApiIgnore @SortDefault(value = BatchResultItem.FIELD_RESULT_ITEM_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultItemDTO.setTenantId(tenantId);
        Page<BatchResultItemDTO> list = batchResultItemRepository.pageAndSortDTO(pageRequest, batchResultItemDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案结果表-校验项信息明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultItemId",
            value = "批数据方案结果表-校验项信息主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultItemId}")
    public ResponseEntity<?> detail(@PathVariable Long resultItemId) {
        BatchResultItemDTO batchResultItemDTO = batchResultItemRepository.selectDTOByPrimaryKeyAndTenant(resultItemId);
        return Results.success(batchResultItemDTO);
    }

    @ApiOperation(value = "创建批数据方案结果表-校验项信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultItemDTO batchResultItemDTO) {
        batchResultItemDTO.setTenantId(tenantId);
        this.validObject(batchResultItemDTO);
        batchResultItemRepository.insertDTOSelective(batchResultItemDTO);
        return Results.success(batchResultItemDTO);
    }

    @ApiOperation(value = "修改批数据方案结果表-校验项信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultItemDTO batchResultItemDTO) {
        batchResultItemRepository.updateDTOWhereTenant(batchResultItemDTO, tenantId);
        return Results.success(batchResultItemDTO);
    }

    @ApiOperation(value = "删除批数据方案结果表-校验项信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchResultItemDTO batchResultItemDTO) {
        batchResultItemDTO.setTenantId(tenantId);
        batchResultItemRepository.deleteByPrimaryKey(batchResultItemDTO);
        return Results.success();
    }
}
