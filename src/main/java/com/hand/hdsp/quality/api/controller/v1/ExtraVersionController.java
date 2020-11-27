package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.ExtraVersionDTO;
import com.hand.hdsp.quality.domain.entity.ExtraVersion;
import com.hand.hdsp.quality.domain.repository.ExtraVersionRepository;
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
 * <p>附加信息版本表 管理 API</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@RestController("extraVersionController.v1")
@RequestMapping("/v1/{organizationId}/extra-versions")
public class ExtraVersionController extends BaseController {

    private ExtraVersionRepository extraVersionRepository;

    public ExtraVersionController(ExtraVersionRepository extraVersionRepository) {
        this.extraVersionRepository = extraVersionRepository;
    }

    @ApiOperation(value = "附加信息版本表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  ExtraVersionDTO extraVersionDTO, @ApiIgnore @SortDefault(value = ExtraVersion.FIELD_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        extraVersionDTO.setTenantId(tenantId);
        Page<ExtraVersionDTO> list = extraVersionRepository.pageAndSortDTO(pageRequest, extraVersionDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "附加信息版本表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "versionId",
            value = "附加信息版本表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{versionId}")
    public ResponseEntity<?> detail(@PathVariable Long versionId) {
        ExtraVersionDTO extraVersionDTO = extraVersionRepository.selectDTOByPrimaryKeyAndTenant(versionId);
        return Results.success(extraVersionDTO);
    }

    @ApiOperation(value = "创建附加信息版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody ExtraVersionDTO extraVersionDTO) {
        extraVersionDTO.setTenantId(tenantId);
        this.validObject(extraVersionDTO);
        extraVersionRepository.insertDTOSelective(extraVersionDTO);
        return Results.success(extraVersionDTO);
    }

    @ApiOperation(value = "修改附加信息版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody ExtraVersionDTO extraVersionDTO) {
                extraVersionRepository.updateDTOWhereTenant(extraVersionDTO, tenantId);
        return Results.success(extraVersionDTO);
    }

    @ApiOperation(value = "删除附加信息版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody ExtraVersionDTO extraVersionDTO) {
                extraVersionDTO.setTenantId(tenantId);
        extraVersionRepository.deleteByPrimaryKey(extraVersionDTO);
        return Results.success();
    }
}
