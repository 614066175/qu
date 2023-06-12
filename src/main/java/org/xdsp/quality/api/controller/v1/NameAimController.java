package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.NameAimDTO;
import org.xdsp.quality.app.service.NameAimService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.repository.NameAimRepository;

import java.util.List;

/**
 * <p>命名落标表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Api(tags = SwaggerTags.NAME_STANDARD_AIM)
@RestController("nameAimController.v1")
@RequestMapping("/v1/{organizationId}/name-aims")
public class NameAimController extends BaseController {

    private final NameAimRepository nameAimRepository;
    private final NameAimService nameAimService;

    public NameAimController(NameAimRepository nameAimRepository, NameAimService nameAimService) {
        this.nameAimRepository = nameAimRepository;
        this.nameAimService = nameAimService;
    }

    @ApiOperation(value = "命名落标表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ),
            @ApiImplicitParam(
                    name = "standardId",
                    value = "落标ID",
                    paramType = "path",
                    required = true
            )
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list/{standardId}")
    public ResponseEntity<List<NameAimDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                                                 @PathVariable(name = "standardId") Long standardId) {
        return Results.success(nameAimRepository.list(standardId));
    }

    @ApiOperation(value = "命名落标表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "aimId",
            value = "命名落标表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{aimId}")
    public ResponseEntity<NameAimDTO> detail(@PathVariable Long aimId) {
        NameAimDTO nameAimDTO = nameAimRepository.selectDTOByPrimaryKeyAndTenant(aimId);
        return Results.success(nameAimDTO);
    }

    @ApiOperation(value = "创建命名落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<NameAimDTO>> create(@PathVariable("organizationId") Long tenantId,
                                                   @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                   @RequestBody List<NameAimDTO> nameAimDTOList) {
        this.validObject(nameAimDTOList);
        return Results.success(nameAimService.batchCreate(nameAimDTOList, tenantId, projectId));
    }

    @ApiOperation(value = "修改命名落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<List<NameAimDTO>> update(@PathVariable("organizationId") Long tenantId,
                                                   @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                   @RequestBody List<NameAimDTO> nameAimDTOList) {
        return Results.success(nameAimService.bitchUpdate(nameAimDTOList, tenantId, projectId));
    }

    @ApiOperation(value = "删除命名落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                       @RequestBody NameAimDTO nameAimDTO) {
        nameAimDTO.setTenantId(tenantId);
        nameAimService.remove(nameAimDTO.getAimId());
        return Results.success();
    }
}
