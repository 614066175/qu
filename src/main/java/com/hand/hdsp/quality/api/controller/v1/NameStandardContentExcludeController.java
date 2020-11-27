package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.NameStandardContentExcludeDTO;
import com.hand.hdsp.quality.domain.entity.NameStandardContentExclude;
import com.hand.hdsp.quality.domain.repository.NameStandardContentExcludeRepository;
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
 * <p>命名标准落标排除表 管理 API</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@RestController("nameStandardContentExcludeController.v1")
@RequestMapping("/v1/{organizationId}/name-standard-content-excludes")
public class NameStandardContentExcludeController extends BaseController {

    private NameStandardContentExcludeRepository nameStandardContentExcludeRepository;

    public NameStandardContentExcludeController(NameStandardContentExcludeRepository nameStandardContentExcludeRepository) {
        this.nameStandardContentExcludeRepository = nameStandardContentExcludeRepository;
    }

    @ApiOperation(value = "命名标准落标排除表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameStandardContentExcludeDTO nameStandardContentExcludeDTO, @ApiIgnore @SortDefault(value = NameStandardContentExclude.FIELD_CONTENT_EXCLUDE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameStandardContentExcludeDTO.setTenantId(tenantId);
        Page<NameStandardContentExcludeDTO> list = nameStandardContentExcludeRepository.pageAndSortDTO(pageRequest, nameStandardContentExcludeDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准落标排除表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "contentExcludeId",
            value = "命名标准落标排除表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{contentExcludeId}")
    public ResponseEntity<?> detail(@PathVariable Long contentExcludeId) {
        NameStandardContentExcludeDTO nameStandardContentExcludeDTO = nameStandardContentExcludeRepository.selectDTOByPrimaryKeyAndTenant(contentExcludeId);
        return Results.success(nameStandardContentExcludeDTO);
    }

    @ApiOperation(value = "创建命名标准落标排除表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardContentExcludeDTO nameStandardContentExcludeDTO) {
        nameStandardContentExcludeDTO.setTenantId(tenantId);
        this.validObject(nameStandardContentExcludeDTO);
        nameStandardContentExcludeRepository.insertDTOSelective(nameStandardContentExcludeDTO);
        return Results.success(nameStandardContentExcludeDTO);
    }

    @ApiOperation(value = "修改命名标准落标排除表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardContentExcludeDTO nameStandardContentExcludeDTO) {
                nameStandardContentExcludeRepository.updateDTOWhereTenant(nameStandardContentExcludeDTO, tenantId);
        return Results.success(nameStandardContentExcludeDTO);
    }

    @ApiOperation(value = "删除命名标准落标排除表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameStandardContentExcludeDTO nameStandardContentExcludeDTO) {
                nameStandardContentExcludeDTO.setTenantId(tenantId);
        nameStandardContentExcludeRepository.deleteByPrimaryKey(nameStandardContentExcludeDTO);
        return Results.success();
    }
}
