package org.xdsp.quality.api.controller.v1;

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
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.NameAimIncludeDTO;
import org.xdsp.quality.domain.entity.NameAimInclude;
import org.xdsp.quality.domain.repository.NameAimIncludeRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>落标包含表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameAimIncludeController.v1")
@RequestMapping("/v1/{organizationId}/name-aim-includes")
public class NameAimIncludeController extends BaseController {

    private final NameAimIncludeRepository nameAimIncludeRepository;

    public NameAimIncludeController(NameAimIncludeRepository nameAimIncludeRepository) {
        this.nameAimIncludeRepository = nameAimIncludeRepository;
    }

    @ApiOperation(value = "落标包含表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<NameAimIncludeDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                        NameAimIncludeDTO nameAimIncludeDTO, @ApiIgnore @SortDefault(value = NameAimInclude.FIELD_INCLUDE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameAimIncludeDTO.setTenantId(tenantId);
        nameAimIncludeDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        Page<NameAimIncludeDTO> list = nameAimIncludeRepository.pageAndSortDTO(pageRequest, nameAimIncludeDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "落标包含表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "includeId",
            value = "落标包含表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{includeId}")
    public ResponseEntity<NameAimIncludeDTO> detail(@PathVariable Long includeId) {
        NameAimIncludeDTO nameAimIncludeDTO = nameAimIncludeRepository.selectDTOByPrimaryKeyAndTenant(includeId);
        return Results.success(nameAimIncludeDTO);
    }

    @ApiOperation(value = "创建落标包含表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<NameAimIncludeDTO> create(@PathVariable("organizationId") Long tenantId,
                                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                    @RequestBody NameAimIncludeDTO nameAimIncludeDTO) {
        nameAimIncludeDTO.setTenantId(tenantId);
        nameAimIncludeDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        this.validObject(nameAimIncludeDTO);
        nameAimIncludeRepository.insertDTOSelective(nameAimIncludeDTO);
        return Results.success(nameAimIncludeDTO);
    }

    @ApiOperation(value = "修改落标包含表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<NameAimIncludeDTO> update(@PathVariable("organizationId") Long tenantId,
                                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                    @RequestBody NameAimIncludeDTO nameAimIncludeDTO) {
        nameAimIncludeDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        nameAimIncludeRepository.updateDTOWhereTenant(nameAimIncludeDTO, tenantId);
        return Results.success(nameAimIncludeDTO);
    }

    @ApiOperation(value = "删除落标包含表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                       @RequestBody NameAimIncludeDTO nameAimIncludeDTO) {
        nameAimIncludeDTO.setTenantId(tenantId);
        nameAimIncludeRepository.deleteByPrimaryKey(nameAimIncludeDTO);
        return Results.success();
    }
}
