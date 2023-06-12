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
import org.xdsp.quality.api.dto.StandardExtraDTO;
import org.xdsp.quality.app.service.StandardExtraService;
import org.xdsp.quality.domain.entity.StandardExtra;
import org.xdsp.quality.domain.repository.StandardExtraRepository;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>标准附加信息表 管理 API</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@RestController("standardExtraController.v1")
@RequestMapping("/v1/{organizationId}/standard-extras")
public class StandardExtraController extends BaseController {

    private StandardExtraRepository standardExtraRepository;
    private StandardExtraService standardExtraService;

    public StandardExtraController(StandardExtraRepository standardExtraRepository, StandardExtraService standardExtraService) {
        this.standardExtraRepository = standardExtraRepository;
        this.standardExtraService = standardExtraService;
    }

    @ApiOperation(value = "标准附加信息表列表")
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
                                  StandardExtraDTO standardExtraDTO, @ApiIgnore @SortDefault(value = StandardExtra.FIELD_EXTRA_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardExtraDTO.setTenantId(tenantId);
        standardExtraDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        Page<StandardExtraDTO> list = standardExtraRepository.pageAndSortDTO(pageRequest, standardExtraDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准附加信息表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "extraId",
            value = "标准附加信息表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{extraId}")
    public ResponseEntity<?> detail(@PathVariable Long extraId) {
        StandardExtraDTO standardExtraDTO = standardExtraRepository.selectDTOByPrimaryKeyAndTenant(extraId);
        return Results.success(standardExtraDTO);
    }

    @ApiOperation(value = "创建标准附加信息表")
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
                                    @RequestBody StandardExtraDTO standardExtraDTO) {
        standardExtraDTO.setTenantId(tenantId);
        standardExtraDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        this.validObject(standardExtraDTO);
        standardExtraRepository.insertDTOSelective(standardExtraDTO);
        return Results.success(standardExtraDTO);
    }

    @ApiOperation(value = "修改标准附加信息表")
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
                                    @RequestBody StandardExtraDTO standardExtraDTO) {
        standardExtraDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        standardExtraRepository.updateDTOWhereTenant(standardExtraDTO, tenantId);
        return Results.success(standardExtraDTO);
    }

    @ApiOperation(value = "删除标准附加信息表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StandardExtraDTO standardExtraDTO) {
        standardExtraDTO.setTenantId(tenantId);
        standardExtraRepository.deleteByPrimaryKey(standardExtraDTO);
        return Results.success();
    }


    @ApiOperation(value = "批量创建标准附加信息表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch-create")
    public ResponseEntity<?> batchCreate(@PathVariable("organizationId") Long tenantId,
                                         @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         @RequestBody List<StandardExtraDTO> standardExtraDTOList) {
        standardExtraDTOList.forEach(standardExtraDTO -> {
            standardExtraDTO.setTenantId(tenantId);
            standardExtraDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        });
        standardExtraRepository.batchInsertDTOSelective(standardExtraDTOList);
        return Results.success(standardExtraDTOList);
    }

    @ApiOperation(value = "批量修改标准附加信息表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/batch-update")
    public ResponseEntity<?> batchUpdate(@PathVariable("organizationId") Long tenantId,
                                         @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         @RequestBody List<StandardExtraDTO> standardExtraDTOList) {
        standardExtraDTOList.forEach(standardExtraDTO ->{
            standardExtraDTO.setTenantId(tenantId);
            standardExtraDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        });
        standardExtraService.batchUpdate(standardExtraDTOList);
        return Results.success(standardExtraDTOList);
    }

    @ApiOperation(value = "删除标准附加信息表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/batch-remove")
    public ResponseEntity<?> batchRemove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                         @RequestBody List<StandardExtraDTO> standardExtraDTOList) {
        standardExtraDTOList.forEach(standardExtraDTO -> standardExtraDTO.setTenantId(tenantId));
        standardExtraRepository.batchDTODeleteByPrimaryKey(standardExtraDTOList);
        return Results.success(standardExtraDTOList);
    }
}
