package com.hand.hdsp.quality.api.controller.v3;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameStandardHistoryLine;
import com.hand.hdsp.quality.api.dto.NameStandardHistoryLineDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardHistoryLineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>命名标准执行历史行表 管理 API</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@RestController("nameStandardHistoryLineController.v1")
@RequestMapping("/v1/{organizationId}/name-standard-history-lines")
public class NameStandardHistoryLineController extends BaseController {

    private NameStandardHistoryLineRepository nameStandardHistoryLineRepository;

    public NameStandardHistoryLineController(NameStandardHistoryLineRepository nameStandardHistoryLineRepository) {
        this.nameStandardHistoryLineRepository = nameStandardHistoryLineRepository;
    }

    @ApiOperation(value = "命名标准执行历史行表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameStandardHistoryLineDTO nameStandardHistoryLineDTO, @ApiIgnore @SortDefault(value = NameStandardHistoryLine.FIELD_HISTORY_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameStandardHistoryLineDTO.setTenantId(tenantId);
        Page<NameStandardHistoryLineDTO> list = nameStandardHistoryLineRepository.pageAndSortDTO(pageRequest, nameStandardHistoryLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准执行历史行表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "historyLineId",
            value = "命名标准执行历史行表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{historyLineId}")
    public ResponseEntity<?> detail(@PathVariable Long historyLineId) {
        NameStandardHistoryLineDTO nameStandardHistoryLineDTO = nameStandardHistoryLineRepository.selectDTOByPrimaryKeyAndTenant(historyLineId);
        return Results.success(nameStandardHistoryLineDTO);
    }

    @ApiOperation(value = "创建命名标准执行历史行表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardHistoryLineDTO nameStandardHistoryLineDTO) {
        nameStandardHistoryLineDTO.setTenantId(tenantId);
        this.validObject(nameStandardHistoryLineDTO);
        nameStandardHistoryLineRepository.insertDTOSelective(nameStandardHistoryLineDTO);
        return Results.success(nameStandardHistoryLineDTO);
    }

    @ApiOperation(value = "修改命名标准执行历史行表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardHistoryLineDTO nameStandardHistoryLineDTO) {
                nameStandardHistoryLineRepository.updateDTOWhereTenant(nameStandardHistoryLineDTO, tenantId);
        return Results.success(nameStandardHistoryLineDTO);
    }

    @ApiOperation(value = "删除命名标准执行历史行表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameStandardHistoryLineDTO nameStandardHistoryLineDTO) {
                nameStandardHistoryLineDTO.setTenantId(tenantId);
        nameStandardHistoryLineRepository.deleteByPrimaryKey(nameStandardHistoryLineDTO);
        return Results.success();
    }
}
