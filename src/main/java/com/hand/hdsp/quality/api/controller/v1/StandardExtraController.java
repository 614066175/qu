package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
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
 * <p>标准额外信息表 管理 API</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
 */
@RestController("standardExtraController.v1")
@RequestMapping("/v1/{organizationId}/standard-extras")
public class StandardExtraController extends BaseController {

    private StandardExtraRepository standardExtraRepository;

    public StandardExtraController(StandardExtraRepository standardExtraRepository) {
        this.standardExtraRepository = standardExtraRepository;
    }

    @ApiOperation(value = "标准额外信息表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  StandardExtraDTO standardExtraDTO, @ApiIgnore @SortDefault(value = StandardExtra.FIELD_EXTRA_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardExtraDTO.setTenantId(tenantId);
        Page<StandardExtraDTO> list = standardExtraRepository.pageAndSortDTO(pageRequest, standardExtraDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准额外信息表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "extraId",
            value = "标准额外信息表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{extraId}")
    public ResponseEntity<?> detail(@PathVariable Long extraId) {
        StandardExtraDTO standardExtraDTO = standardExtraRepository.selectDTOByPrimaryKeyAndTenant(extraId);
        return Results.success(standardExtraDTO);
    }

    @ApiOperation(value = "创建标准额外信息表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody StandardExtraDTO standardExtraDTO) {
        standardExtraDTO.setTenantId(tenantId);
        this.validObject(standardExtraDTO);
        standardExtraRepository.insertDTOSelective(standardExtraDTO);
        return Results.success(standardExtraDTO);
    }

    @ApiOperation(value = "修改标准额外信息表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StandardExtraDTO standardExtraDTO) {
                standardExtraRepository.updateDTOWhereTenant(standardExtraDTO, tenantId);
        return Results.success(standardExtraDTO);
    }

    @ApiOperation(value = "删除标准额外信息表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StandardExtraDTO standardExtraDTO) {
                standardExtraDTO.setTenantId(tenantId);
        standardExtraRepository.deleteByPrimaryKey(standardExtraDTO);
        return Results.success();
    }
}
