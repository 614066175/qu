package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.LovValueVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovValueVersion;
import com.hand.hdsp.quality.domain.repository.LovValueVersionRepository;
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
 * <p>LOV独立值集表 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@RestController("lovValueVersionController.v1")
@RequestMapping("/v1/{organizationId}/lov-value-versions")
public class LovValueVersionController extends BaseController {

    private LovValueVersionRepository lovValueVersionRepository;

    public LovValueVersionController(LovValueVersionRepository lovValueVersionRepository) {
        this.lovValueVersionRepository = lovValueVersionRepository;
    }

    @ApiOperation(value = "LOV独立值集表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                LovValueVersionDTO lovValueVersionDTO, @ApiIgnore @SortDefault(value = LovValueVersion.FIELD_VALUE_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        lovValueVersionDTO.setTenantId(tenantId);
        Page<LovValueVersionDTO> list = lovValueVersionRepository.pageAndSortDTO(pageRequest, lovValueVersionDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "LOV独立值集表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "valueVersionId",
            value = "LOV独立值集表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{valueVersionId}")
    public ResponseEntity<?> detail(@PathVariable Long valueVersionId) {
        LovValueVersionDTO lovValueVersionDTO = lovValueVersionRepository.selectDTOByPrimaryKeyAndTenant(valueVersionId);
        return Results.success(lovValueVersionDTO);
    }

    @ApiOperation(value = "创建LOV独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LovValueVersionDTO lovValueVersionDTO) {
        lovValueVersionDTO.setTenantId(tenantId);
        this.validObject(lovValueVersionDTO);
        lovValueVersionRepository.insertDTOSelective(lovValueVersionDTO);
        return Results.success(lovValueVersionDTO);
    }

    @ApiOperation(value = "修改LOV独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LovValueVersionDTO lovValueVersionDTO) {
                lovValueVersionRepository.updateDTOWhereTenant(lovValueVersionDTO, tenantId);
        return Results.success(lovValueVersionDTO);
    }

    @ApiOperation(value = "删除LOV独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody LovValueVersionDTO lovValueVersionDTO) {
                lovValueVersionDTO.setTenantId(tenantId);
        lovValueVersionRepository.deleteByPrimaryKey(lovValueVersionDTO);
        return Results.success();
    }
}
