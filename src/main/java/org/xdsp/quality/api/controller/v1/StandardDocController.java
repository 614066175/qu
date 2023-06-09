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
import org.springframework.web.multipart.MultipartFile;
import org.xdsp.core.constant.HdspConstant;
import org.xdsp.quality.api.dto.StandardDocDTO;
import org.xdsp.quality.app.service.StandardDocService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.StandardDoc;
import org.xdsp.quality.domain.repository.StandardDocRepository;
import org.xdsp.quality.infra.export.dto.DocStandardExportDTO;
import org.xdsp.quality.infra.mapper.StandardDocMapper;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>标准文档管理表 管理 API</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Api(tags = SwaggerTags.STANDARD_DOC)
@RestController("standardDocController.v1")
@RequestMapping("/v1/{organizationId}/standard-docs")
public class StandardDocController extends BaseController {
    private final StandardDocRepository standardDocRepository;
    private final StandardDocService standardDocService;
    private final StandardDocMapper standardDocMapper;

    public StandardDocController(StandardDocRepository standardDocRepository, StandardDocService standardDocService, StandardDocMapper standardDocMapper) {
        this.standardDocRepository = standardDocRepository;
        this.standardDocService = standardDocService;
        this.standardDocMapper = standardDocMapper;
    }

    @ApiOperation(value = "标准文档管理表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<StandardDocDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                                                     StandardDocDTO standardDocDTO,
                                                     @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                     @ApiIgnore @SortDefault(value = StandardDoc.FIELD_DOC_ID,
                                                             direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardDocDTO.setTenantId(tenantId);
        standardDocDTO.setProjectId(HdspConstant.DEFAULT_PROJECT_ID);
        Page<StandardDocDTO> list = standardDocService.list(pageRequest, standardDocDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准文档管理表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "standardDocId",
            value = "标准文档管理表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{standardDocId}")
    public ResponseEntity<StandardDocDTO> detail(@PathVariable Long standardDocId) {
        StandardDocDTO standardDocDTO = standardDocRepository.selectDTOByPrimaryKey(standardDocId);
        return Results.success(standardDocDTO);
    }

    @ApiOperation(value = "创建标准文档管理表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<StandardDocDTO> create(@ApiParam(value = "租户id", required = true) @PathVariable("organizationId") Long tenantId,
                                                 @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                 @RequestPart StandardDocDTO standardDocDTO,
                                                 @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        standardDocDTO.setTenantId(tenantId);
        standardDocDTO.setProjectId(HdspConstant.DEFAULT_PROJECT_ID);
        this.validObject(standardDocDTO);
        return Results.success(standardDocService.create(standardDocDTO, multipartFile));
    }

    @ApiOperation(value = "修改标准文档管理表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/update")
    public ResponseEntity<StandardDocDTO> update(@PathVariable("organizationId") Long tenantId,
                                                 @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                 @RequestPart StandardDocDTO standardDocDTO,
                                                 @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        standardDocDTO.setTenantId(tenantId);
        standardDocDTO.setProjectId(projectId);
        return Results.success(standardDocService.update(standardDocDTO, multipartFile));
    }

    @ApiOperation(value = "删除标准文档管理表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                       @RequestBody List<StandardDocDTO> standardDocDTOList) {
        standardDocService.remove(standardDocDTOList, tenantId);
        return Results.success();
    }

    @ApiOperation(value = "下载标准文档")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/download-standard-doc")
    public void download(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                         @RequestBody StandardDocDTO standardDocDTO,
                         HttpServletResponse response) {
        standardDocDTO.setTenantId(tenantId);
        standardDocService.downloadStandardDoc(standardDocDTO, response);
    }

    @ApiOperation(value = "导出标准标准文档")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = DocStandardExportDTO.class)
    public ResponseEntity<List<DocStandardExportDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                       @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                       StandardDocDTO dto,
                                                       ExportParam exportParam,
                                                       HttpServletResponse response) {
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        dto.setTenantId(tenantId);
        dto.setProjectId(HdspConstant.DEFAULT_PROJECT_ID);
        List<DocStandardExportDTO> dtoList =
                standardDocService.export(dto, exportParam);
        return Results.success(dtoList);
    }

    @ApiOperation(value = "预览接口url")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/preview-url")
    public ResponseEntity<?> previewUrl(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId) {
        return Results.success(standardDocService.previewUrl());
    }

}