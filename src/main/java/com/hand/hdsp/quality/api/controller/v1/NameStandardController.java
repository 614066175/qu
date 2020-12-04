package com.hand.hdsp.quality.api.controller.v1;

import java.util.List;

import com.hand.hdsp.quality.app.service.NameStandardService;
import com.hand.hdsp.quality.config.SwaggerTags;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>命名标准表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Api(tags = SwaggerTags.NAME_STANDARD)
@RestController("nameStandardController.v1")
@RequestMapping("/v1/{organizationId}/name-standards")
public class NameStandardController extends BaseController {

    private final NameStandardRepository nameStandardRepository;
    private final NameStandardService nameStandardService;

    public NameStandardController(NameStandardRepository nameStandardRepository,
                                  NameStandardService nameStandardService) {
        this.nameStandardRepository = nameStandardRepository;
        this.nameStandardService = nameStandardService;
    }

    @ApiOperation(value = "命名标准表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameStandardDTO nameStandardDTO, @ApiIgnore @SortDefault(value = NameStandard.FIELD_STANDARD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameStandardDTO.setTenantId(tenantId);
        Page<NameStandardDTO> list = PageHelper.doPage(pageRequest,()->nameStandardRepository.list(nameStandardDTO));
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "standardId",
            value = "命名标准表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{standardId}")
    public ResponseEntity<?> detail(@PathVariable Long standardId) {
        NameStandardDTO nameStandardDTO = nameStandardRepository.selectDTOByPrimaryKeyAndTenant(standardId);
        return Results.success(nameStandardDTO);
    }

    @ApiOperation(value = "创建命名标准表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardDTO nameStandardDTO) {
        nameStandardDTO.setTenantId(tenantId);
        this.validObject(nameStandardDTO);
        nameStandardRepository.insertDTOSelective(nameStandardDTO);
        return Results.success(nameStandardDTO);
    }

    @ApiOperation(value = "修改命名标准表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<NameStandardDTO> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardDTO nameStandardDTO) {
        return Results.success(nameStandardService.update(nameStandardDTO));
    }

    @ApiOperation(value = "删除命名标准表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody List<NameStandardDTO> nameStandardDtoList) {
                nameStandardDtoList.forEach(x->x.setTenantId(tenantId));
                nameStandardService.bitchRemove(nameStandardDtoList);
        return Results.success();
    }
}
