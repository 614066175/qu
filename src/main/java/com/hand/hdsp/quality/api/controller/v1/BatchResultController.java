package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
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
 * <p>批数据方案结果表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@RestController("batchResultController.v1")
@RequestMapping("/v1/{organizationId}/batch-results")
public class BatchResultController extends BaseController {

    private BatchResultRepository batchResultRepository;

    public BatchResultController(BatchResultRepository batchResultRepository) {
        this.batchResultRepository = batchResultRepository;
    }

    @ApiOperation(value = "批数据方案结果表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchResultDTO batchResultDTO, @ApiIgnore @SortDefault(value = BatchResult.FIELD_RESULT_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultDTO.setTenantId(tenantId);
        Page<BatchResultDTO> list = batchResultRepository.pageAndSortDTO(pageRequest, batchResultDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案结果表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultId",
            value = "批数据方案结果表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultId}")
    public ResponseEntity<?> detail(@PathVariable Long resultId) {
        BatchResultDTO batchResultDTO = batchResultRepository.selectDTOByPrimaryKeyAndTenant(resultId);
        return Results.success(batchResultDTO);
    }

    @ApiOperation(value = "创建批数据方案结果表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultDTO batchResultDTO) {
        batchResultDTO.setTenantId(tenantId);
        this.validObject(batchResultDTO);
        batchResultRepository.insertDTOSelective(batchResultDTO);
        return Results.success(batchResultDTO);
    }

    @ApiOperation(value = "修改批数据方案结果表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultDTO batchResultDTO) {
        batchResultRepository.updateDTOWhereTenant(batchResultDTO, tenantId);
        return Results.success(batchResultDTO);
    }

    @ApiOperation(value = "删除批数据方案结果表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchResultDTO batchResultDTO) {
        batchResultDTO.setTenantId(tenantId);
        batchResultRepository.deleteByPrimaryKey(batchResultDTO);
        return Results.success();
    }
}
