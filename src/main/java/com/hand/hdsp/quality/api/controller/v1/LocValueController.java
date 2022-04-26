package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.LocValueDTO;
import com.hand.hdsp.quality.app.service.LocValueService;
import com.hand.hdsp.quality.domain.entity.LocValue;
import com.hand.hdsp.quality.domain.repository.LocValueRepository;
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

import java.util.List;

/**
 * <p>loc独立值集表 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@RestController("locValueController.v1")
@RequestMapping("/v1/{organizationId}/loc-values")
public class LocValueController extends BaseController {

    private final LocValueRepository locValueRepository;
    private final LocValueService locValueSerVice;

    public LocValueController(LocValueRepository locValueRepository,LocValueService locValueSerVice) {
        this.locValueRepository = locValueRepository;
        this.locValueSerVice=locValueSerVice;
    }

    @ApiOperation(value = "loc独立值集表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                LocValueDTO locValueDTO, @ApiIgnore @SortDefault(value = LocValue.FIELD_LOC_VALUE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        locValueDTO.setTenantId(tenantId);
        return Results.success(locValueSerVice.list(pageRequest,locValueDTO));
    }

    @ApiOperation(value = "loc独立值集表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "locValueId",
            value = "loc独立值集表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{locValueId}")
    public ResponseEntity<?> detail(@PathVariable Long locValueId) {
        LocValueDTO locValueDTO = locValueRepository.selectDTOByPrimaryKeyAndTenant(locValueId);
        return Results.success(locValueDTO);
    }

    @ApiOperation(value = "创建loc独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LocValueDTO locValueDTO) {
        locValueDTO.setTenantId(tenantId);
        this.validObject(locValueDTO);
        locValueRepository.insertDTOSelective(locValueDTO);
        return Results.success(locValueDTO);
    }

    @ApiOperation(value = "修改loc独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LocValueDTO locValueDTO) {
                locValueRepository.updateDTOWhereTenant(locValueDTO, tenantId);
        return Results.success(locValueDTO);
    }

    @ApiOperation(value = "删除loc独立值集表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody List<LocValueDTO> locValueDTOs) {
        locValueDTOs.forEach(lv -> lv.setTenantId(tenantId));
        locValueRepository.batchDTODelete(locValueDTOs);

        return Results.success();
    }

}
