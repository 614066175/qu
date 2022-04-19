package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.domain.entity.LovValue;
import com.hand.hdsp.quality.domain.repository.LovValueRepository;
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
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@RestController("lovValueController.v1")
@RequestMapping("/v1/{organizationId}/lov-values")
public class LovValueController extends BaseController {

    private LovValueRepository lovValueRepository;

    public LovValueController(LovValueRepository lovValueRepository) {
        this.lovValueRepository = lovValueRepository;
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
                LovValueDTO lovValueDTO, @ApiIgnore @SortDefault(value = LovValue.FIELD_LOV_VALUE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        lovValueDTO.setTenantId(tenantId);
        Page<LovValueDTO> list = lovValueRepository.pageAndSortDTO(pageRequest, lovValueDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "LOV独立值集表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "lovValueId",
            value = "LOV独立值集表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{lovValueId}")
    public ResponseEntity<?> detail(@PathVariable Long lovValueId) {
        LovValueDTO lovValueDTO = lovValueRepository.selectDTOByPrimaryKeyAndTenant(lovValueId);
        return Results.success(lovValueDTO);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LovValueDTO lovValueDTO) {
        lovValueDTO.setTenantId(tenantId);
        this.validObject(lovValueDTO);
        lovValueRepository.insertDTOSelective(lovValueDTO);
        return Results.success(lovValueDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LovValueDTO lovValueDTO) {
                lovValueRepository.updateDTOWhereTenant(lovValueDTO, tenantId);
        return Results.success(lovValueDTO);
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
                                    @RequestBody LovValueDTO lovValueDTO) {
                lovValueDTO.setTenantId(tenantId);
        lovValueRepository.deleteByPrimaryKey(lovValueDTO);
        return Results.success();
    }
}
