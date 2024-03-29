package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.NameStandardDTO;
import org.xdsp.quality.app.service.NameStandardService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.NameStandard;
import org.xdsp.quality.domain.repository.NameStandardRepository;
import org.xdsp.quality.infra.export.dto.NameStandardExportDTO;
import org.xdsp.quality.infra.mapper.StandardDocMapper;
import org.xdsp.quality.infra.vo.NameStandardDatasourceVO;
import org.xdsp.quality.infra.vo.NameStandardTableVO;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>命名标准表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Api(tags = SwaggerTags.NAME_STANDARD)
@RestController("nameStandardController.v1")
@RequestMapping("/v1/{organizationId}/name-standards")
public class NameStandardController extends BaseController {

    private final NameStandardRepository nameStandardRepository;
    private final NameStandardService nameStandardService;
    private final StandardDocMapper standardDocMapper;

    public NameStandardController(NameStandardRepository nameStandardRepository,
                                  NameStandardService nameStandardService, StandardDocMapper standardDocMapper) {
        this.nameStandardRepository = nameStandardRepository;
        this.nameStandardService = nameStandardService;
        this.standardDocMapper = standardDocMapper;
    }

    @ApiOperation(value = "命名标准表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<NameStandardDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                                                      @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                      NameStandardDTO nameStandardDTO, @ApiIgnore @SortDefault(value = NameStandard.FIELD_STANDARD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameStandardDTO.setTenantId(tenantId);
        nameStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        Page<NameStandardDTO> list = nameStandardService.pageNameStandards(nameStandardDTO,pageRequest);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "standardId",
            value = "命名标准表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{standardId}")
    public ResponseEntity<NameStandardDTO> detail(@PathVariable Long standardId) {
        NameStandardDTO nameStandardDTO = nameStandardRepository.detail(standardId);
        return Results.success(nameStandardDTO);
    }

    @ApiOperation(value = "创建命名标准表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<NameStandardDTO> create(@PathVariable("organizationId") Long tenantId,
                                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                  @RequestBody NameStandardDTO nameStandardDTO) {
        nameStandardDTO.setTenantId(tenantId);
        nameStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        this.validObject(nameStandardDTO);
        return Results.success(nameStandardService.create(nameStandardDTO));
    }

    @ApiOperation(value = "修改命名标准表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<NameStandardDTO> update(@PathVariable("organizationId") Long tenantId,
                                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                  @RequestBody NameStandardDTO nameStandardDTO) {
        nameStandardDTO.setTenantId(tenantId);
        nameStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        return Results.success(nameStandardService.update(nameStandardDTO));
    }

    @ApiOperation(value = "删除命名标准表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                       @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                       @RequestBody List<NameStandardDTO> nameStandardDtoList) {
        nameStandardDtoList.forEach(x -> {
            x.setTenantId(tenantId);
            x.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        });
        nameStandardService.bitchRemove(nameStandardDtoList);
        return Results.success();
    }

    @ApiOperation(value = "批量执行标准")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/execute")
    public ResponseEntity<Void> execute(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                        @RequestBody List<Long> nameStandardIdList) {
        nameStandardService.batchExecuteStandard(nameStandardIdList);
        return Results.success();
    }

    @ApiOperation(value = "命名标准导出")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = NameStandardExportDTO.class)
    public ResponseEntity<List<NameStandardExportDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                        NameStandardDTO dto,
                                                        ExportParam exportParam,
                                                        HttpServletResponse response) {

        dto.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dto.setTenantId(tenantId);
        List<NameStandardExportDTO> dtoList = nameStandardService.export(dto, exportParam);
        return Results.success(dtoList);
    }

    @ApiOperation(value = "获取表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/tables")
    public ResponseEntity<List<NameStandardTableVO>> getTables(NameStandardDatasourceVO nameStandardDatasourceVO) {
        return Results.success(nameStandardService.getTables(nameStandardDatasourceVO));
    }
}
