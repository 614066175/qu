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
import org.xdsp.quality.api.dto.BaseFormValueDTO;
import org.xdsp.quality.domain.entity.BaseFormValue;
import org.xdsp.quality.domain.repository.BaseFormValueRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>质检项表单值 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@RestController("baseFormValueController.v1")
@RequestMapping("/v1/{organizationId}/base-form-values")
public class BaseFormValueController extends BaseController {

    private BaseFormValueRepository baseFormValueRepository;

    public BaseFormValueController(BaseFormValueRepository baseFormValueRepository) {
        this.baseFormValueRepository = baseFormValueRepository;
    }

    @ApiOperation(value = "质检项表单值列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  BaseFormValueDTO baseFormValueDTO, @ApiIgnore @SortDefault(value = BaseFormValue.FIELD_RELATION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        baseFormValueDTO.setTenantId(tenantId);
        Page<BaseFormValueDTO> list = baseFormValueRepository.pageAndSortDTO(pageRequest, baseFormValueDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "质检项表单值明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "relationId",
            value = "质检项表单值主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{relationId}")
    public ResponseEntity<?> detail(@PathVariable Long relationId) {
        BaseFormValueDTO baseFormValueDTO = baseFormValueRepository.selectDTOByPrimaryKeyAndTenant(relationId);
        return Results.success(baseFormValueDTO);
    }

    @ApiOperation(value = "创建质检项表单值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BaseFormValueDTO baseFormValueDTO) {
        baseFormValueDTO.setTenantId(tenantId);
        this.validObject(baseFormValueDTO);
        baseFormValueRepository.insertDTOSelective(baseFormValueDTO);
        return Results.success(baseFormValueDTO);
    }

    @ApiOperation(value = "修改质检项表单值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BaseFormValueDTO baseFormValueDTO) {
        baseFormValueRepository.updateDTOWhereTenant(baseFormValueDTO, tenantId);
        return Results.success(baseFormValueDTO);
    }

    @ApiOperation(value = "删除质检项表单值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BaseFormValueDTO baseFormValueDTO) {
        baseFormValueDTO.setTenantId(tenantId);
        baseFormValueRepository.deleteByPrimaryKey(baseFormValueDTO);
        return Results.success();
    }
}
