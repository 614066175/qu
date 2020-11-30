package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.api.dto.NameAimDTO;
import com.hand.hdsp.quality.domain.repository.NameAimRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>命名落标表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameAimController.v1")
@RequestMapping("/v1/{organizationId}/name-aims")
public class NameAimController extends BaseController {

    private NameAimRepository nameAimRepository;

    public NameAimController(NameAimRepository nameAimRepository) {
        this.nameAimRepository = nameAimRepository;
    }

    @ApiOperation(value = "命名落标表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameAimDTO nameAimDTO, @ApiIgnore @SortDefault(value = NameAim.FIELD_AIM_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameAimDTO.setTenantId(tenantId);
        Page<NameAimDTO> list = nameAimRepository.pageAndSortDTO(pageRequest, nameAimDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名落标表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "aimId",
            value = "命名落标表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{aimId}")
    public ResponseEntity<?> detail(@PathVariable Long aimId) {
        NameAimDTO nameAimDTO = nameAimRepository.selectDTOByPrimaryKeyAndTenant(aimId);
        return Results.success(nameAimDTO);
    }

    @ApiOperation(value = "创建命名落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameAimDTO nameAimDTO) {
        nameAimDTO.setTenantId(tenantId);
        this.validObject(nameAimDTO);
        nameAimRepository.insertDTOSelective(nameAimDTO);
        return Results.success(nameAimDTO);
    }

    @ApiOperation(value = "修改命名落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameAimDTO nameAimDTO) {
                nameAimRepository.updateDTOWhereTenant(nameAimDTO, tenantId);
        return Results.success(nameAimDTO);
    }

    @ApiOperation(value = "删除命名落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameAimDTO nameAimDTO) {
                nameAimDTO.setTenantId(tenantId);
        nameAimRepository.deleteByPrimaryKey(nameAimDTO);
        return Results.success();
    }
}
