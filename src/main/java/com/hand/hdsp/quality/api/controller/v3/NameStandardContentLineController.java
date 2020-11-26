package com.hand.hdsp.quality.api.controller.v3;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameStandardContentLine;
import com.hand.hdsp.quality.api.dto.NameStandardContentLineDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardContentLineRepository;
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
 * <p>命名标准落标行表 管理 API</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@RestController("nameStandardContentLineController.v1")
@RequestMapping("/v1/{organizationId}/name-standard-content-lines")
public class NameStandardContentLineController extends BaseController {

    private NameStandardContentLineRepository nameStandardContentLineRepository;

    public NameStandardContentLineController(NameStandardContentLineRepository nameStandardContentLineRepository) {
        this.nameStandardContentLineRepository = nameStandardContentLineRepository;
    }

    @ApiOperation(value = "命名标准落标行表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameStandardContentLineDTO nameStandardContentLineDTO, @ApiIgnore @SortDefault(value = NameStandardContentLine.FIELD_CONTENT_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameStandardContentLineDTO.setTenantId(tenantId);
        Page<NameStandardContentLineDTO> list = nameStandardContentLineRepository.pageAndSortDTO(pageRequest, nameStandardContentLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准落标行表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "contentLineId",
            value = "命名标准落标行表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{contentLineId}")
    public ResponseEntity<?> detail(@PathVariable Long contentLineId) {
        NameStandardContentLineDTO nameStandardContentLineDTO = nameStandardContentLineRepository.selectDTOByPrimaryKeyAndTenant(contentLineId);
        return Results.success(nameStandardContentLineDTO);
    }

    @ApiOperation(value = "创建命名标准落标行表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardContentLineDTO nameStandardContentLineDTO) {
        nameStandardContentLineDTO.setTenantId(tenantId);
        this.validObject(nameStandardContentLineDTO);
        nameStandardContentLineRepository.insertDTOSelective(nameStandardContentLineDTO);
        return Results.success(nameStandardContentLineDTO);
    }

    @ApiOperation(value = "修改命名标准落标行表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardContentLineDTO nameStandardContentLineDTO) {
                nameStandardContentLineRepository.updateDTOWhereTenant(nameStandardContentLineDTO, tenantId);
        return Results.success(nameStandardContentLineDTO);
    }

    @ApiOperation(value = "删除命名标准落标行表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameStandardContentLineDTO nameStandardContentLineDTO) {
                nameStandardContentLineDTO.setTenantId(tenantId);
        nameStandardContentLineRepository.deleteByPrimaryKey(nameStandardContentLineDTO);
        return Results.success();
    }
}
