package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.app.service.StandardGroupService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.repository.StandardGroupRepository;
import org.xdsp.quality.infra.vo.StandardGroupVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 20:57
 * @since 1.0
 */
@Api(tags = SwaggerTags.STANDARD_GROUP)
@RestController("standardGroupController.v1")
@RequestMapping("/v1/{organizationId}/standard-group")
public class StandardGroupController extends BaseController {

    private final StandardGroupService standardGroupService;
    private final StandardGroupRepository standardGroupRepository;

    public StandardGroupController(StandardGroupService standardGroupService,
                                   StandardGroupRepository standardGroupRepository) {
        this.standardGroupService = standardGroupService;
        this.standardGroupRepository = standardGroupRepository;
    }

    @ApiOperation(value = "根据分组查询标准")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  StandardGroupVO standardGroupVO , PageRequest pageRequest) {
        standardGroupVO.setTenantId(tenantId);
        return Results.success(standardGroupService.list(pageRequest,standardGroupVO));
    }

    @ApiOperation(value = "根据标准名找到对应分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/group")
    public ResponseEntity<?> group(@PathVariable(name = "organizationId") Long tenantId,
                                   @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                   StandardGroupVO standardGroupVO) {
        standardGroupVO.setTenantId(tenantId);
        standardGroupVO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        return Results.success(standardGroupService.listByGroup(standardGroupVO));
    }

    @ApiOperation(value = "创建分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody StandardGroupDTO standardGroupDTO) {
        standardGroupDTO.setTenantId(tenantId);
        standardGroupDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        return Results.success(standardGroupService.create(standardGroupDTO));
    }

    @ApiOperation(value = "修改分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> updateGroup(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         @RequestBody StandardGroupDTO standardGroupDTO) {
        standardGroupDTO.setTenantId(tenantId);
        standardGroupDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        return Results.success(standardGroupService.update(standardGroupDTO));
    }

    @ApiOperation(value = "删除分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody StandardGroupDTO standardGroupDTO) {
        standardGroupDTO.setTenantId(tenantId);
        standardGroupDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        standardGroupService.delete(standardGroupDTO);
        return Results.success(standardGroupDTO);
    }

    @ApiOperation(value = "导出标准分组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = StandardGroupDTO.class)
    public ResponseEntity<?> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    StandardGroupDTO dto,
                                    ExportParam exportParam,
                                    HttpServletResponse response) {
        dto.setTenantId(tenantId);
        List<StandardGroupDTO> dtoList =
                standardGroupService.export(dto, exportParam);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        return Results.success(dtoList);
    }
}
