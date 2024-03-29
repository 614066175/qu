package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.app.service.PlanGroupService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.infra.vo.PlanGroupTreeVO;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>评估方案分组表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Api(tags = SwaggerTags.PLAN_GROUP)
@RestController("planGroupController.v1")
@RequestMapping("/v1/{organizationId}/plan-groups")
public class PlanGroupController extends BaseController {

    private final PlanGroupRepository planGroupRepository;
    private final PlanGroupService planGroupService;

    public PlanGroupController(PlanGroupRepository planGroupRepository,
                               PlanGroupService planGroupService) {
        this.planGroupRepository = planGroupRepository;
        this.planGroupService = planGroupService;
    }

    @ApiOperation(value = "评估方案分组所有列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/all")
    public ResponseEntity<?> allList(@PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                     PlanGroup planGroup) {
        planGroup.setTenantId(tenantId);
        planGroup.setProjectId(projectId);
        List<PlanGroup> planGroups = planGroupRepository.select(planGroup);
        return Results.success(planGroups);
    }

    @ApiOperation(value = "评估方案分组表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  PlanGroupDTO planGroupDTO, @ApiIgnore @SortDefault(value = PlanGroup.FIELD_GROUP_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        planGroupDTO.setTenantId(tenantId);
        planGroupDTO.setProjectId(projectId);
        Page<PlanGroupDTO> list = planGroupRepository.pageAndSortDTO(pageRequest, planGroupDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "评估方案分组所有列表（含评估方案）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/tree")
    public ResponseEntity<?> tree(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  PlanGroupTreeVO planGroupTreeVO) {
        planGroupTreeVO.setTenantId(tenantId);
        planGroupTreeVO.setProjectId(projectId);
        return Results.success(planGroupRepository.tree(planGroupTreeVO));
    }

    @ApiOperation(value = "评估方案分组表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "groupId",
            value = "评估方案分组表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{groupId}")
    public ResponseEntity<?> detail(@PathVariable Long groupId) {
        PlanGroupDTO planGroupDTO = planGroupRepository.selectDTOByPrimaryKeyAndTenant(groupId);
        return Results.success(planGroupDTO);
    }

    @ApiOperation(value = "创建评估方案分组表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody PlanGroupDTO planGroupDTO) {
        planGroupDTO.setTenantId(tenantId);
        planGroupDTO.setProjectId(projectId);
        this.validObject(planGroupDTO);
        planGroupService.create(planGroupDTO);
//        planGroupRepository.insertDTOSelective(planGroupDTO);
        return Results.success(planGroupDTO);
    }

    @ApiOperation(value = "修改评估方案分组表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody PlanGroupDTO planGroupDTO) {
        planGroupDTO.setProjectId(projectId);
        planGroupRepository.updateDTOAllColumnWhereTenant(planGroupDTO, tenantId);
        return Results.success(planGroupDTO);
    }

    @ApiOperation(value = "删除评估方案分组表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody PlanGroupDTO planGroupDTO) {
        planGroupDTO.setTenantId(tenantId);
        planGroupService.delete(planGroupDTO);
        return Results.success();
    }

    @ApiOperation(value = "从分组开始导出评估方案")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = PlanGroupDTO.class)
    public ResponseEntity<?> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    PlanGroupDTO dto,
                                    ExportParam exportParam,
                                    HttpServletResponse response) {
        dto.setTenantId(tenantId);
        List<PlanGroupDTO> dtoList = planGroupService.export(dto, exportParam);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        return Results.success(dtoList);
    }
}
