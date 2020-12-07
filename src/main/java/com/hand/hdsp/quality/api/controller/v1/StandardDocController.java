package com.hand.hdsp.quality.api.controller.v1;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.app.service.StandardDocService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
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
import springfox.documentation.annotations.ApiIgnore;

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

    public StandardDocController(StandardDocRepository standardDocRepository, StandardDocService standardDocService) {
        this.standardDocRepository = standardDocRepository;
        this.standardDocService = standardDocService;
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
                                                     @ApiIgnore @SortDefault(value = StandardDoc.FIELD_DOC_ID,
                                                             direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardDocDTO.setTenantId(tenantId);
        Page<StandardDocDTO> list = standardDocRepository.pageAndSortDTO(pageRequest, standardDocDTO);
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
                                                 @RequestPart StandardDocDTO standardDocDTO,
                                                 @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        standardDocDTO.setTenantId(tenantId);
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
    @PutMapping
    public ResponseEntity<StandardDocDTO> update(@PathVariable("organizationId") Long tenantId,
                                                 @RequestPart StandardDocDTO standardDocDTO,
                                                 @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        standardDocDTO.setTenantId(tenantId);
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
    @GetMapping("/download-standard-doc")
    public void download(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                         StandardDocDTO standardDocDTO,
                         HttpServletResponse response) {
        standardDocDTO.setTenantId(tenantId);
        standardDocService.downloadStandardDoc(standardDocDTO, response);
    }

    @ApiOperation(value = "导出标准标准文档")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = StandardDocDTO.class)
    public ResponseEntity<List<StandardDocDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                       StandardDocDTO dto,
                                                       ExportParam exportParam,
                                                       HttpServletResponse response,
                                                       @ApiIgnore @SortDefault(value = StandardDoc.FIELD_DOC_ID,
                                                               direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dto.setTenantId(tenantId);
        List<StandardDocDTO> dtoList =
                standardDocService.export(dto, exportParam, pageRequest);
        return Results.success(dtoList);
    }

}