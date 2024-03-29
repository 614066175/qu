package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.api.dto.RuleGroupDTO;
import org.xdsp.quality.app.service.RuleGroupService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.RuleGroup;
import org.xdsp.quality.domain.repository.RuleGroupRepository;
import org.xdsp.quality.infra.export.dto.StandardRuleExportDTO;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 规则分组表 管理 API
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 12:03
 */
@Slf4j
@Api(tags = SwaggerTags.RULE_GROUP)
@RestController("RuleGroupController.v1")
@RequestMapping("/v1/{organizationId}/rule-groups")
public class RuleGroupController extends BaseController {

    private final RuleGroupService ruleGroupService;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleGroupController(RuleGroupService ruleGroupService, RuleGroupRepository ruleGroupRepository) {
        this.ruleGroupService = ruleGroupService;
        this.ruleGroupRepository = ruleGroupRepository;
    }


    @ApiOperation(value = "规则分组表列表")
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
                                  RuleGroupDTO ruleGroupDTO, @ApiIgnore @SortDefault(value = RuleGroup.FIELD_GROUP_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleGroupDTO.setTenantId(tenantId);
        ruleGroupDTO.setProjectId(projectId);
        Page<RuleGroupDTO> list = ruleGroupRepository.pageAndSortDTO(pageRequest, ruleGroupDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则分组表列表标准规则租户级（不分页）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listNoPage(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        RuleGroup ruleGroup) {
        ruleGroup.setTenantId(tenantId);
        ruleGroup.setProjectId(projectId);
        return Results.success(ruleGroupService.listNoPage(ruleGroup));
    }

    @ApiOperation(value = "规则分组表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "groupId",
            value = "规则分组表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{groupId}")
    public ResponseEntity<?> detail(@PathVariable Long groupId) {
        RuleGroupDTO ruleGroupDTO = ruleGroupRepository.selectDTOByPrimaryKeyAndTenant(groupId);
        return Results.success(ruleGroupDTO);
    }

    @ApiOperation(value = "创建规则分组表")
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
                                    @RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupDTO.setTenantId(tenantId);
        ruleGroupDTO.setProjectId(projectId);
        validObject(ruleGroupDTO);
        ruleGroupRepository.insertDTOSelective(ruleGroupDTO);
        return Results.success(ruleGroupDTO);
    }

    @ApiOperation(value = "修改规则分组表")
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
                                    @RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupDTO.setProjectId(projectId);
        ruleGroupRepository.updateDTOAllColumnWhereTenant(ruleGroupDTO, tenantId);
        return Results.success(ruleGroupDTO);
    }

    @ApiOperation(value = "删除规则分组表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupDTO.setTenantId(tenantId);
        ruleGroupService.delete(ruleGroupDTO);
        return Results.success();
    }

    @ApiOperation(value = "从分组开始导出标准规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = StandardRuleExportDTO.class)
    public ResponseEntity<?> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    RuleDTO dto,
                                    ExportParam exportParam,
                                    HttpServletResponse response) {
        dto.setTenantId(tenantId);
        dto.setProjectId(projectId);
        List<StandardRuleExportDTO> dtoList =
                ruleGroupService.export(dto, exportParam);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        return Results.success(dtoList);
    }
}
