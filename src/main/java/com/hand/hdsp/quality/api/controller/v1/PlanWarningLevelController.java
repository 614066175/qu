package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.domain.repository.PlanWarningLevelRepository;
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
 * <p>方案告警等级表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@RestController("planWarningLevelController.v1")
@RequestMapping("/v1/{organizationId}/plan-warning-levels")
public class PlanWarningLevelController extends BaseController {

    private PlanWarningLevelRepository planWarningLevelRepository;

    public PlanWarningLevelController(PlanWarningLevelRepository planWarningLevelRepository) {
        this.planWarningLevelRepository = planWarningLevelRepository;
    }

    @ApiOperation(value = "方案告警等级表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  PlanWarningLevelDTO planWarningLevelDTO, @ApiIgnore @SortDefault(value = PlanWarningLevel.FIELD_LEVEL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        planWarningLevelDTO.setTenantId(tenantId);
        Page<PlanWarningLevelDTO> list = planWarningLevelRepository.pageAndSortDTO(pageRequest, planWarningLevelDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "方案告警等级表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "levelId",
            value = "方案告警等级表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{levelId}")
    public ResponseEntity<?> detail(@PathVariable Long levelId) {
        PlanWarningLevelDTO planWarningLevelDTO = planWarningLevelRepository.selectDTOByPrimaryKeyAndTenant(levelId);
        return Results.success(planWarningLevelDTO);
    }

    @ApiOperation(value = "创建方案告警等级表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody PlanWarningLevelDTO planWarningLevelDTO) {
        planWarningLevelDTO.setTenantId(tenantId);
        this.validObject(planWarningLevelDTO);
        planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
        return Results.success(planWarningLevelDTO);
    }

    @ApiOperation(value = "修改方案告警等级表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody PlanWarningLevelDTO planWarningLevelDTO) {
        planWarningLevelRepository.updateDTOWhereTenant(planWarningLevelDTO, tenantId);
        return Results.success(planWarningLevelDTO);
    }

    @ApiOperation(value = "删除方案告警等级表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody PlanWarningLevelDTO planWarningLevelDTO) {
        planWarningLevelDTO.setTenantId(tenantId);
        planWarningLevelRepository.deleteByPrimaryKey(planWarningLevelDTO);
        return Results.success();
    }
}
