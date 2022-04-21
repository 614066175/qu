package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.app.service.LovService;
import com.hand.hdsp.quality.domain.entity.Lov;
import com.hand.hdsp.quality.domain.repository.LovRepository;
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
 * <p>LOV表 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@RestController("lovController.v1")
@RequestMapping("/v1/{organizationId}/lovs")
public class LovController extends BaseController {

    private LovRepository lovRepository;
    private final LovService lovService;

    public LovController(LovRepository lovRepository, LovService lovService) {
        this.lovRepository = lovRepository;
        this.lovService = lovService;
    }

    @ApiOperation(value = "LOV表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  LovDTO lovDTO, @ApiIgnore @SortDefault(value = Lov.FIELD_LOV_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        lovDTO.setTenantId(tenantId);
        Page<LovDTO> list = lovRepository.pageAndSortDTO(pageRequest, lovDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "LOV表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "lovId",
            value = "LOV表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{lovId}")
    public ResponseEntity<?> detail(@PathVariable Long lovId) {
        LovDTO lovDTO = lovRepository.selectDTOByPrimaryKeyAndTenant(lovId);
        return Results.success(lovDTO);
    }

    @ApiOperation(value = "创建LOV表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LovDTO lovDTO) {
        lovDTO.setTenantId(tenantId);
        this.validObject(lovDTO);
        lovRepository.insertDTOSelective(lovDTO);
        return Results.success(lovDTO);
    }

    @ApiOperation(value = "修改LOV表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LovDTO lovDTO) {
        lovRepository.updateDTOWhereTenant(lovDTO, tenantId);
        return Results.success(lovDTO);
    }



    @ApiOperation(value = "删除LOV表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody LovDTO lovDTO) {
        lovDTO.setTenantId(tenantId);
        lovRepository.deleteByPrimaryKey(lovDTO);
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
    @PostMapping("/lov-release")
    public ResponseEntity<?> lovRelease(@PathVariable("organizationId") Long tenantId, Long lovId) {
        return Results.success(lovService.lovRelease(lovId));
    }

    @ApiOperation(value = "是否启用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/open")
    public ResponseEntity<?> open(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    Long lovId) {

        return Results.success(lovService.AssertOpen(lovId));
    }

}
