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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.HdspConstant;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.app.service.RuleService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.Rule;
import org.xdsp.quality.domain.repository.RuleRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Api(tags = SwaggerTags.RULE)
@RestController("ruleController.v1")
@RequestMapping("/v1/{organizationId}/rules")
public class RuleController extends BaseController {

    private final RuleRepository ruleRepository;
    private final RuleService ruleService;

    public RuleController(RuleRepository ruleRepository, RuleService ruleService) {
        this.ruleRepository = ruleRepository;
        this.ruleService = ruleService;
    }

    @ApiOperation(value = "规则表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  RuleDTO ruleDTO,
                                  @ApiIgnore @SortDefault(value = Rule.FIELD_RULE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleDTO.setTenantId(tenantId);
        ruleDTO.setProjectId(projectId);
        if (ruleDTO.getGroupId() != null && ruleDTO.getGroupId() == 0) {
            ruleDTO.setGroupId(null);
        }
        Page<RuleDTO> list = ruleRepository.pageAndSortDTO(pageRequest, ruleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则表列表（标准规则租户级）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listTenant(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        RuleDTO ruleDTO,
                                        @ApiIgnore @SortDefault(value = Rule.FIELD_RULE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleDTO.setTenantId(tenantId);
        ruleDTO.setProjectId(projectId);
        Page<RuleDTO> list = ruleService.pageRules(pageRequest, ruleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则表明细（标准规则租户级）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "ruleId",
            value = "规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/{ruleId}")
    public ResponseEntity<?> detail(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @PathVariable Long ruleId) {
        RuleDTO ruleDTO = ruleService.detail(ruleId, tenantId,projectId);
        return Results.success(ruleDTO);
    }

    @ApiOperation(value = "创建规则表")
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
                                    @RequestBody RuleDTO ruleDTO) {
        ruleDTO.setTenantId(tenantId);
        ruleDTO.setProjectId(projectId);
        this.validObject(ruleDTO);
        ruleService.insert(ruleDTO);
        return Results.success(ruleDTO);
    }

    @ApiOperation(value = "修改规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody RuleDTO ruleDTO) {
        ruleDTO.setTenantId(tenantId);
        ruleDTO.setProjectId(projectId);
        ruleService.update(ruleDTO);
        return Results.success(ruleDTO);
    }

    @ApiOperation(value = "删除规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody RuleDTO ruleDTO) {
        ruleDTO.setTenantId(tenantId);
        ruleService.delete(ruleDTO);
        return Results.success();
    }

}
