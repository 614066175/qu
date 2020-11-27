package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.StandardOutbibDTO;
import com.hand.hdsp.quality.domain.entity.StandardOutbib;
import com.hand.hdsp.quality.domain.repository.StandardOutbibRepository;
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
 * <p>标准落标表 管理 API</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
 */
@RestController("standardOutbibController.v1")
@RequestMapping("/v1/{organizationId}/standard-outbibs")
public class StandardOutbibController extends BaseController {

    private StandardOutbibRepository standardOutbibRepository;

    public StandardOutbibController(StandardOutbibRepository standardOutbibRepository) {
        this.standardOutbibRepository = standardOutbibRepository;
    }

    @ApiOperation(value = "标准落标表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  StandardOutbibDTO standardOutbibDTO, @ApiIgnore @SortDefault(value = StandardOutbib.FIELD_OUTBIB_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardOutbibDTO.setTenantId(tenantId);
        Page<StandardOutbibDTO> list = standardOutbibRepository.pageAndSortDTO(pageRequest, standardOutbibDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准落标表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "outbibId",
            value = "标准落标表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{outbibId}")
    public ResponseEntity<?> detail(@PathVariable Long outbibId) {
        StandardOutbibDTO standardOutbibDTO = standardOutbibRepository.selectDTOByPrimaryKeyAndTenant(outbibId);
        return Results.success(standardOutbibDTO);
    }

    @ApiOperation(value = "创建标准落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody StandardOutbibDTO standardOutbibDTO) {
        standardOutbibDTO.setTenantId(tenantId);
        this.validObject(standardOutbibDTO);
        standardOutbibRepository.insertDTOSelective(standardOutbibDTO);
        return Results.success(standardOutbibDTO);
    }

    @ApiOperation(value = "修改标准落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StandardOutbibDTO standardOutbibDTO) {
                standardOutbibRepository.updateDTOWhereTenant(standardOutbibDTO, tenantId);
        return Results.success(standardOutbibDTO);
    }

    @ApiOperation(value = "删除标准落标表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StandardOutbibDTO standardOutbibDTO) {
                standardOutbibDTO.setTenantId(tenantId);
        standardOutbibRepository.deleteByPrimaryKey(standardOutbibDTO);
        return Results.success();
    }
}
