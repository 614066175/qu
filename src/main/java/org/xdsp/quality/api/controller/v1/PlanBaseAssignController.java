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
import org.xdsp.core.constant.HdspConstant;
import org.xdsp.quality.api.dto.PlanBaseAssignDTO;
import org.xdsp.quality.app.service.PlanBaseAssignService;
import org.xdsp.quality.domain.entity.PlanBaseAssign;
import org.xdsp.quality.domain.repository.PlanBaseAssignRepository;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>质检项分配表 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@RestController("planBaseAssignController.v1")
@RequestMapping("/v1/{organizationId}/plan-base-assigns")
public class PlanBaseAssignController extends BaseController {

    private PlanBaseAssignRepository planBaseAssignRepository;
    private final PlanBaseAssignService planBaseAssignService;

    public PlanBaseAssignController(PlanBaseAssignRepository planBaseAssignRepository, PlanBaseAssignService planBaseAssignService) {
        this.planBaseAssignRepository = planBaseAssignRepository;
        this.planBaseAssignService = planBaseAssignService;
    }

    @ApiOperation(value = "质检项分配表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  PlanBaseAssignDTO planBaseAssignDTO, @ApiIgnore @SortDefault(value = PlanBaseAssign.FIELD_BASE_ASSIGN_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        planBaseAssignDTO.setTenantId(tenantId);
        Page<PlanBaseAssignDTO> list = planBaseAssignRepository.pageAndSortDTO(pageRequest, planBaseAssignDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "质检项分配表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "baseAssignId",
            value = "质检项分配表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{baseAssignId}")
    public ResponseEntity<?> detail(@PathVariable Long baseAssignId) {
        PlanBaseAssignDTO planBaseAssignDTO = planBaseAssignRepository.selectDTOByPrimaryKeyAndTenant(baseAssignId);
        return Results.success(planBaseAssignDTO);
    }

    @ApiOperation(value = "创建质检项分配表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody List<PlanBaseAssignDTO> planBaseAssignDTOList) {
        planBaseAssignDTOList.forEach(planBaseAssignDTO -> {
            planBaseAssignDTO.setTenantId(tenantId);
            planBaseAssignDTO.setProjectId(projectId);
        });
        planBaseAssignRepository.batchInsertDTOSelective(planBaseAssignDTOList);
        return Results.success(planBaseAssignDTOList);
    }

    @ApiOperation(value = "修改质检项分配表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody PlanBaseAssignDTO planBaseAssignDTO) {
        planBaseAssignRepository.updateDTOWhereTenant(planBaseAssignDTO, tenantId);
        return Results.success(planBaseAssignDTO);
    }

    @ApiOperation(value = "删除质检项分配表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody PlanBaseAssignDTO planBaseAssignDTO) {
        planBaseAssignDTO.setTenantId(tenantId);
        planBaseAssignRepository.deleteByPrimaryKey(planBaseAssignDTO);
        return Results.success();
    }

    @ApiOperation(value = "质检项分配")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/base-assign/{planBaseId}")
    public ResponseEntity<?> baseAssign(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        @PathVariable(name = "planBaseId") Long planBaseId,
                                        @RequestBody List<PlanBaseAssignDTO> planBaseAssignDTOList) {
        planBaseAssignDTOList.forEach(planBaseAssignDTO -> {
            planBaseAssignDTO.setTenantId(tenantId);
            planBaseAssignDTO.setProjectId(projectId);
        });
        planBaseAssignService.baseAssign(planBaseId, planBaseAssignDTOList);
        return Results.success(planBaseAssignDTOList);
    }


}
