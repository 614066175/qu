package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.LocValueVersionDTO;
import com.hand.hdsp.quality.app.service.LocValueVersionService;
import com.hand.hdsp.quality.domain.entity.LocValueVersion;
import com.hand.hdsp.quality.domain.repository.LocValueVersionRepository;
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
 * <p>loc独立值集表 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@RestController("locValueVersionController.v1")
@RequestMapping("/v1/{organizationId}/loc-value-versions")
public class LocValueVersionController extends BaseController {

    private LocValueVersionRepository locValueVersionRepository;
    private LocValueVersionService locValueVersionService;

    public LocValueVersionController(LocValueVersionRepository locValueVersionRepository, LocValueVersionService locValueVersionService) {
        this.locValueVersionRepository = locValueVersionRepository;
        this.locValueVersionService = locValueVersionService;
    }

    @ApiOperation(value = "loc独立值集表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                LocValueVersionDTO locValueVersionDTO, @ApiIgnore @SortDefault(value = LocValueVersion.FIELD_VALUE_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        locValueVersionDTO.setTenantId(tenantId);
        return Results.success(locValueVersionService.list(pageRequest,locValueVersionDTO));
    }

    @ApiOperation(value = "loc独立值集表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "valueVersionId",
            value = "loc独立值集表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{valueVersionId}")
    public ResponseEntity<?> detail(@PathVariable Long valueVersionId) {
        LocValueVersionDTO locValueVersionDTO = locValueVersionRepository.selectDTOByPrimaryKeyAndTenant(valueVersionId);
        return Results.success(locValueVersionDTO);
    }

    @ApiOperation(value = "创建loc独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LocValueVersionDTO locValueVersionDTO) {
        locValueVersionDTO.setTenantId(tenantId);
        this.validObject(locValueVersionDTO);
        locValueVersionRepository.insertDTOSelective(locValueVersionDTO);
        return Results.success(locValueVersionDTO);
    }

    @ApiOperation(value = "修改loc独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LocValueVersionDTO locValueVersionDTO) {
                locValueVersionRepository.updateDTOWhereTenant(locValueVersionDTO, tenantId);
        return Results.success(locValueVersionDTO);
    }

    @ApiOperation(value = "删除loc独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody LocValueVersionDTO locValueVersionDTO) {
                locValueVersionDTO.setTenantId(tenantId);
        locValueVersionRepository.deleteByPrimaryKey(locValueVersionDTO);
        return Results.success();
    }
}
