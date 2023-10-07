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
import org.xdsp.quality.api.dto.RootDicDTO;
import org.xdsp.quality.domain.entity.RootDic;
import org.xdsp.quality.domain.repository.RootDicRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p> 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
@RestController("rootDicController.v1")
@RequestMapping("/v1/{organizationId}/root-dics")
public class RootDicController extends BaseController {

    private RootDicRepository rootDicRepository;

    public RootDicController(RootDicRepository rootDicRepository) {
        this.rootDicRepository = rootDicRepository;
    }

    @ApiOperation(value = "列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                RootDicDTO rootDicDTO, @ApiIgnore @SortDefault(value = RootDic.FIELD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        rootDicDTO.setTenantId(tenantId);
        Page<RootDicDTO> list = rootDicRepository.pageAndSortDTO(pageRequest, rootDicDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "id",
            value = "主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        RootDicDTO rootDicDTO = rootDicRepository.selectDTOByPrimaryKeyAndTenant(id);
        return Results.success(rootDicDTO);
    }

    @ApiOperation(value = "创建")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody RootDicDTO rootDicDTO) {
        rootDicDTO.setTenantId(tenantId);
        this.validObject(rootDicDTO);
        rootDicRepository.insertDTOSelective(rootDicDTO);
        return Results.success(rootDicDTO);
    }

    @ApiOperation(value = "修改")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody RootDicDTO rootDicDTO) {
                rootDicRepository.updateDTOWhereTenant(rootDicDTO, tenantId);
        return Results.success(rootDicDTO);
    }

    @ApiOperation(value = "删除")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody RootDicDTO rootDicDTO) {
                rootDicDTO.setTenantId(tenantId);
        rootDicRepository.deleteByPrimaryKey(rootDicDTO);
        return Results.success();
    }
}
