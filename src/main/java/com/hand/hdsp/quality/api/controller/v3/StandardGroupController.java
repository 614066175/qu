package com.hand.hdsp.quality.api.controller.v3;

import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.app.service.StandardGroupService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.vo.StandardGroupVO;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 20:57
 * @since 1.0
 */
@Api(tags = SwaggerTags.STANDARD_GROUP)
@RestController("standardGroupController.v3")
@RequestMapping("/v3/{organizationId}/standard-group")
public class StandardGroupController extends BaseController {

    private final StandardGroupService standardGroupService;
    private final StandardGroupRepository standardGroupRepository;

    public StandardGroupController(StandardGroupService standardGroupService,
                                   StandardGroupRepository standardGroupRepository) {
        this.standardGroupService = standardGroupService;
        this.standardGroupRepository = standardGroupRepository;
    }

    @ApiOperation(value = "根据标准名找到对应分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId, StandardGroupVO standardGroupVO , PageRequest pageRequest) {
        return Results.success(standardGroupService.list(pageRequest,standardGroupVO));
    }

    @ApiOperation(value = "根据标准名找到对应分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/group")
    public ResponseEntity<?> group(@PathVariable(name = "organizationId") Long tenantId, StandardGroupVO standardGroupVO) {
        return Results.success(standardGroupService.listByGroup(standardGroupVO));
    }

    @ApiOperation(value = "创建分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable(name = "organizationId") Long tenantId, StandardGroupDTO standardGroupDTO) {
        standardGroupDTO.setTenantId(tenantId);
        this.validObject(standardGroupDTO);
        return Results.success(standardGroupRepository.insertDTOSelective(standardGroupDTO));
    }

    @ApiOperation(value = "修改分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> updateGroup(@PathVariable(name = "organizationId") Long tenantId, @RequestBody StandardGroupDTO standardGroupDTO) {
        standardGroupDTO.setTenantId(tenantId);
        standardGroupRepository.updateByDTOPrimaryKeySelective(standardGroupDTO);
        return Results.success(standardGroupDTO);
    }

    @ApiOperation(value = "删除分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable(name = "organizationId") Long tenantId, @RequestBody StandardGroupDTO standardGroupDTO) {
        standardGroupDTO.setTenantId(tenantId);
        standardGroupService.delete(standardGroupDTO);
        return Results.success(standardGroupDTO);
    }
}
