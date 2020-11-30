package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameAimExclude;
import com.hand.hdsp.quality.api.dto.NameAimExcludeDTO;
import com.hand.hdsp.quality.domain.repository.NameAimExcludeRepository;
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
 * <p>落标排除表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameAimExcludeController.v1")
@RequestMapping("/v1/{organizationId}/name-aim-excludes")
public class NameAimExcludeController extends BaseController {

    private NameAimExcludeRepository nameAimExcludeRepository;

    public NameAimExcludeController(NameAimExcludeRepository nameAimExcludeRepository) {
        this.nameAimExcludeRepository = nameAimExcludeRepository;
    }

    @ApiOperation(value = "落标排除表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameAimExcludeDTO nameAimExcludeDTO, @ApiIgnore @SortDefault(value = NameAimExclude.FIELD_EXCLUDE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameAimExcludeDTO.setTenantId(tenantId);
        Page<NameAimExcludeDTO> list = nameAimExcludeRepository.pageAndSortDTO(pageRequest, nameAimExcludeDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "落标排除表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "excludeId",
            value = "落标排除表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{excludeId}")
    public ResponseEntity<?> detail(@PathVariable Long excludeId) {
        NameAimExcludeDTO nameAimExcludeDTO = nameAimExcludeRepository.selectDTOByPrimaryKeyAndTenant(excludeId);
        return Results.success(nameAimExcludeDTO);
    }

    @ApiOperation(value = "创建落标排除表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameAimExcludeDTO nameAimExcludeDTO) {
        nameAimExcludeDTO.setTenantId(tenantId);
        this.validObject(nameAimExcludeDTO);
        nameAimExcludeRepository.insertDTOSelective(nameAimExcludeDTO);
        return Results.success(nameAimExcludeDTO);
    }

    @ApiOperation(value = "修改落标排除表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameAimExcludeDTO nameAimExcludeDTO) {
                nameAimExcludeRepository.updateDTOWhereTenant(nameAimExcludeDTO, tenantId);
        return Results.success(nameAimExcludeDTO);
    }

    @ApiOperation(value = "删除落标排除表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameAimExcludeDTO nameAimExcludeDTO) {
                nameAimExcludeDTO.setTenantId(tenantId);
        nameAimExcludeRepository.deleteByPrimaryKey(nameAimExcludeDTO);
        return Results.success();
    }
}
