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
import org.xdsp.quality.api.dto.LocDTO;
import org.xdsp.quality.app.service.LocService;
import org.xdsp.quality.domain.entity.Loc;
import org.xdsp.quality.domain.repository.LocRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>loc表 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@RestController("locController.v1")
@RequestMapping("/v1/{organizationId}/locs")
public class LocController extends BaseController {

    private LocRepository locRepository;
    private LocService locService;

    public LocController(LocRepository locRepository, LocService locService) {
        this.locRepository = locRepository;
        this.locService = locService;
    }

    @ApiOperation(value = "loc表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  LocDTO locDTO, @ApiIgnore @SortDefault(value = Loc.FIELD_LOC_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        locDTO.setTenantId(tenantId);
        Page<LocDTO> list = locRepository.pageAndSortDTO(pageRequest,locDTO);
        return Results.success(list);
    }


    @ApiOperation(value = "loc表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/all")
    public ResponseEntity<?> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                  LocDTO locDTO) {
        locDTO.setTenantId(tenantId);

        return Results.success(locService.listAll(locDTO));
    }



    @ApiOperation(value = "loc表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "locId",
            value = "loc表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{locId}")
    public ResponseEntity<?> detail(@PathVariable String organizationId, @PathVariable Long locId) {
        LocDTO locDTO = locService.detail(locId);
        return Results.success(locDTO);
    }

    @ApiOperation(value = "创建loc表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LocDTO locDTO) {
        locDTO.setTenantId(tenantId);
        this.validObject(locDTO);
        locRepository.insertDTOSelective(locDTO);
        return Results.success(locDTO);
    }

    @ApiOperation(value = "修改loc表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LocDTO locDTO) {
        locRepository.updateDTOWhereTenant(locDTO, tenantId);
        return Results.success(locDTO);
    }


    @ApiOperation(value = "删除loc表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody LocDTO locDTO) {
        locDTO.setTenantId(tenantId);
        locRepository.deleteByPrimaryKey(locDTO);
        return Results.success();
    }

    @ApiOperation(value = "代码集发布")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/loc-release")
    public ResponseEntity<?> locRelease(@PathVariable("organizationId") Long tenantId, Long locId) {
        return Results.success(locService.locRelease(locId));
    }

}
