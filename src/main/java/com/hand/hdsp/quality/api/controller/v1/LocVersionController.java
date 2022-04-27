package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.app.service.LocVersionService;
import com.hand.hdsp.quality.domain.entity.LocVersion;
import com.hand.hdsp.quality.domain.repository.LocVersionRepository;
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
 * <p>loc表 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@RestController("locVersionController.v1")
@RequestMapping("/v1/{organizationId}/loc-versions")
public class LocVersionController extends BaseController {

    private LocVersionRepository locVersionRepository;
    private LocVersionService locVersionService;

    public LocVersionController(LocVersionRepository locVersionRepository, LocVersionService locVersionService) {
        this.locVersionRepository = locVersionRepository;
        this.locVersionService = locVersionService;
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
                                  LocVersionDTO locVersionDTO, @ApiIgnore @SortDefault(value = LocVersion.FIELD_LOC_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        locVersionDTO.setTenantId(tenantId);
        Page<LocVersionDTO> list = locVersionRepository.pageAndSortDTO(pageRequest, locVersionDTO);
        for (LocVersionDTO versionDTO : list) {
            versionDTO.setUpdaterName(locVersionService.getUserName(versionDTO.getLastUpdatedBy())) ;
        }
        return Results.success(list);
    }

    @ApiOperation(value = "loc表列表-查询所有")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list-all")
    public ResponseEntity<?> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                     LocVersionDTO locVersionDTO) {
        locVersionDTO.setTenantId(tenantId);
        return Results.success(locVersionService.listAll(locVersionDTO));
    }

    @ApiOperation(value = "loc表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "versionId",
            value = "loc表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{versionId}")
    public ResponseEntity<?> detail(@PathVariable Long versionId) {
        LocVersionDTO locVersionDTO = locVersionRepository.selectDTOByPrimaryKeyAndTenant(versionId);
        return Results.success(locVersionDTO);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LocVersionDTO locVersionDTO) {
        locVersionDTO.setTenantId(tenantId);
        this.validObject(locVersionDTO);
        locVersionRepository.insertDTOSelective(locVersionDTO);
        return Results.success(locVersionDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LocVersionDTO locVersionDTO) {
        locVersionRepository.updateDTOWhereTenant(locVersionDTO, tenantId);
        return Results.success(locVersionDTO);
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
                                    @RequestBody LocVersionDTO locVersionDTO) {
        locVersionDTO.setTenantId(tenantId);
        locVersionRepository.deleteByPrimaryKey(locVersionDTO);
        return Results.success();
    }


}
