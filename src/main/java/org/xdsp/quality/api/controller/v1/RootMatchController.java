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
import org.springframework.web.multipart.MultipartFile;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.RootMatchDTO;
import org.xdsp.quality.app.service.RootMatchService;
import org.xdsp.quality.domain.entity.RootMatch;
import org.xdsp.quality.domain.repository.RootMatchRepository;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>字段标准匹配表 管理 API</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@RestController("rootMatchController.v1")
@RequestMapping("/v1/{organizationId}/root-matchs")
public class RootMatchController extends BaseController {

    private final RootMatchRepository rootMatchRepository;
    private final RootMatchService rootMatchService;

    public RootMatchController(RootMatchRepository rootMatchRepository,
                               RootMatchService rootMatchService) {
        this.rootMatchRepository = rootMatchRepository;
        this.rootMatchService = rootMatchService;
    }

    @ApiOperation(value = "字段标准匹配表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  RootMatchDTO rootMatchDTO,
                                  @ApiIgnore @SortDefault(value = RootMatch.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        rootMatchDTO.setTenantId(tenantId);
        rootMatchDTO.setProjectId(projectId);
        Page<RootMatchDTO> list = rootMatchService.pageRootMatch(pageRequest, rootMatchDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "字段标准匹配表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "id",
            value = "字段标准匹配表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        RootMatchDTO rootMatchDTO = rootMatchRepository.selectDTOByPrimaryKeyAndTenant(id);
        return Results.success(rootMatchDTO);
    }

    @ApiOperation(value = "创建字段标准匹配表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody RootMatchDTO rootMatchDTO) {
        rootMatchDTO.setTenantId(tenantId);
        rootMatchDTO.setProjectId(projectId);
        this.validObject(rootMatchDTO);
        rootMatchRepository.insertDTOSelective(rootMatchDTO);
        return Results.success(rootMatchDTO);
    }

    @ApiOperation(value = "修改字段标准匹配表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody RootMatchDTO rootMatchDTO) {
        rootMatchDTO.setTenantId(tenantId);
        rootMatchDTO.setProjectId(projectId);
        return Results.success(rootMatchService.update(rootMatchDTO));
    }

    @ApiOperation(value = "删除字段标准匹配表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody RootMatchDTO rootMatchDTO) {
        rootMatchDTO.setTenantId(tenantId);
        rootMatchDTO.setProjectId(projectId);
        rootMatchRepository.deleteByPrimaryKey(rootMatchDTO);
        return Results.success();
    }


    @ApiOperation(value = "智能匹配")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/smart-match")
    public ResponseEntity<?> smartMatch(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        @RequestBody RootMatchDTO rootMatchDTO) {
        rootMatchDTO.setProjectId(projectId);
        rootMatchDTO.setTenantId(tenantId);
        return Results.success(rootMatchService.smartMatch(rootMatchDTO));
    }

    @ApiOperation(value = "导入excel")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    RootMatchDTO rootMatchDTO,
                                    @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        rootMatchDTO.setTenantId(tenantId);
        rootMatchDTO.setProjectId(projectId);
        return Results.success(rootMatchService.upload(rootMatchDTO, file));
    }


    @ApiOperation(value = "导出excel")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/export")
    public ResponseEntity<?> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    RootMatchDTO rootMatchDTO,
                                    HttpServletResponse response) {
        rootMatchDTO.setProjectId(projectId);
        rootMatchDTO.setTenantId(tenantId);
        rootMatchService.export(rootMatchDTO,response);
        return Results.success();
    }
}
