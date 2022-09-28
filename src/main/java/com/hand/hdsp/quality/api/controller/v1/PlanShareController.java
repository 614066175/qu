package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.PlanShareDTO;
import com.hand.hdsp.quality.domain.entity.PlanShare;
import com.hand.hdsp.quality.domain.repository.PlanShareRepository;
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
 * <p> 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@RestController("planShareController.v1")
@RequestMapping("/v1/{organizationId}/plan-shares")
public class PlanShareController extends BaseController {

    private PlanShareRepository planShareRepository;

    public PlanShareController(PlanShareRepository planShareRepository) {
        this.planShareRepository = planShareRepository;
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
                PlanShareDTO planShareDTO, @ApiIgnore @SortDefault(value = PlanShare.FIELD_SHARE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        planShareDTO.setTenantId(tenantId);
        Page<PlanShareDTO> list = planShareRepository.pageAndSortDTO(pageRequest, planShareDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "shareId",
            value = "主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{shareId}")
    public ResponseEntity<?> detail(@PathVariable Long shareId) {
        PlanShareDTO planShareDTO = planShareRepository.selectDTOByPrimaryKeyAndTenant(shareId);
        return Results.success(planShareDTO);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody PlanShareDTO planShareDTO) {
        planShareDTO.setTenantId(tenantId);
        this.validObject(planShareDTO);
        planShareRepository.insertDTOSelective(planShareDTO);
        return Results.success(planShareDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody PlanShareDTO planShareDTO) {
                planShareRepository.updateDTOWhereTenant(planShareDTO, tenantId);
        return Results.success(planShareDTO);
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
                                    @RequestBody PlanShareDTO planShareDTO) {
                planShareDTO.setTenantId(tenantId);
        planShareRepository.deleteByPrimaryKey(planShareDTO);
        return Results.success();
    }
}
