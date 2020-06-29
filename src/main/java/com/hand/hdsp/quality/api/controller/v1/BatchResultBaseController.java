package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
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

/**
 * <p>批数据方案结果表-表信息 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Api(tags = SwaggerTags.BATCH_RESULT_BASE)
@RestController("batchResultBaseController.v1")
@RequestMapping("/v1/{organizationId}/batch-result-bases")
public class BatchResultBaseController extends BaseController {

    private final BatchResultBaseRepository batchResultBaseRepository;

    public BatchResultBaseController(BatchResultBaseRepository batchResultBaseRepository) {
        this.batchResultBaseRepository = batchResultBaseRepository;
    }

    @ApiOperation(value = "批数据方案结果表-表信息列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BatchResultBaseDTO batchResultBaseDTO, @ApiIgnore @SortDefault(value = BatchResultBase.FIELD_RESULT_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultBaseDTO.setTenantId(tenantId);
        Page<BatchResultBaseDTO> list = batchResultBaseRepository.pageAndSortDTO(pageRequest, batchResultBaseDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "查看评估报告各级规则错误信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("result-base")
    public ResponseEntity<?> resultBase(@PathVariable(name = "organizationId") Long tenantId,
                                        BatchResultBaseDTO batchResultBaseDTO) {
        batchResultBaseDTO.setTenantId(tenantId);
        return Results.success(batchResultBaseRepository.listResultBase(batchResultBaseDTO));
    }

    @ApiOperation(value = "批数据方案结果表-表信息明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultBaseId",
            value = "批数据方案结果表-表信息主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultBaseId}")
    public ResponseEntity<?> detail(@PathVariable Long resultBaseId) {
        BatchResultBaseDTO batchResultBaseDTO = batchResultBaseRepository.selectDTOByPrimaryKeyAndTenant(resultBaseId);
        return Results.success(batchResultBaseDTO);
    }

    @ApiOperation(value = "创建批数据方案结果表-表信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultBaseDTO batchResultBaseDTO) {
        batchResultBaseDTO.setTenantId(tenantId);
        this.validObject(batchResultBaseDTO);
        batchResultBaseRepository.insertDTOSelective(batchResultBaseDTO);
        return Results.success(batchResultBaseDTO);
    }

    @ApiOperation(value = "修改批数据方案结果表-表信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultBaseDTO batchResultBaseDTO) {
        batchResultBaseRepository.updateDTOWhereTenant(batchResultBaseDTO, tenantId);
        return Results.success(batchResultBaseDTO);
    }

    @ApiOperation(value = "删除批数据方案结果表-表信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchResultBaseDTO batchResultBaseDTO) {
        batchResultBaseDTO.setTenantId(tenantId);
        batchResultBaseRepository.deleteByPrimaryKey(batchResultBaseDTO);
        return Results.success();
    }
}
