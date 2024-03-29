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
import org.xdsp.quality.api.dto.StandardAimDTO;
import org.xdsp.quality.app.service.StandardAimService;
import org.xdsp.quality.domain.entity.StandardAim;
import org.xdsp.quality.domain.repository.StandardAimRepository;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>标准落标表 管理 API</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@RestController("standardAimController.v1")
@RequestMapping("/v1/{organizationId}/standard-aims")
public class StandardAimController extends BaseController {

    private StandardAimRepository standardAimRepository;

    private StandardAimService standardAimService;

    public StandardAimController(StandardAimRepository standardAimRepository, StandardAimService standardAimService) {
        this.standardAimRepository = standardAimRepository;
        this.standardAimService = standardAimService;
    }

    @ApiOperation(value = "标准落标表列表")
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
                                  StandardAimDTO standardAimDTO, @ApiIgnore @SortDefault(value = StandardAim.FIELD_AIM_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardAimDTO.setTenantId(tenantId);
        standardAimDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        Page<StandardAimDTO> list = standardAimService.list(pageRequest, standardAimDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准落标表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "aimId",
            value = "标准落标表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{aimId}")
    public ResponseEntity<?> detail(@PathVariable Long aimId) {
        StandardAimDTO standardAimDTO = standardAimRepository.selectDTOByPrimaryKeyAndTenant(aimId);
        return Results.success(standardAimDTO);
    }

    @ApiOperation(value = "创建标准落标表")
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
                                    @RequestBody List<StandardAimDTO> standardAimDTOList) {
        standardAimDTOList.forEach(standardAimDTO -> {standardAimDTO.setTenantId(tenantId);
            standardAimDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
            this.validObject(standardAimDTO);
        });
        return Results.success(standardAimRepository.batchInsertDTOSelective(standardAimDTOList));
    }

    @ApiOperation(value = "修改标准落标表")
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
                                    @RequestBody StandardAimDTO standardAimDTO) {
        standardAimDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        standardAimRepository.updateDTOWhereTenant(standardAimDTO, tenantId);
        return Results.success(standardAimDTO);
    }

    @ApiOperation(value = "删除标准落标表")
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
                                    @RequestBody List<StandardAimDTO> standardAimDTOList) {
        standardAimService.batchDelete(standardAimDTOList, tenantId, projectId);
        return Results.success();
    }

    @ApiOperation(value = "落标查询标准未落标字段")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/un-aimed-field")
    public ResponseEntity<?> unAimedField(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          StandardAimDTO standardAimDTO) {
        standardAimDTO.setTenantId(tenantId);
        standardAimDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        return Results.success(standardAimService.unAimField(standardAimDTO));
    }

    @ApiOperation(value = "反向落标，用于表设计发布")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/reverse-aim")
    public ResponseEntity<?> reverseAim(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        @RequestBody List<StandardAimDTO> standardAimDTOList) {
        standardAimDTOList.forEach(standardAimDTO -> standardAimDTO.setProjectId(projectId));
        return Results.success(standardAimService.reverseAim(tenantId, standardAimDTOList));
    }


}
