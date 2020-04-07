package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.app.service.RuleGroupService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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

    private RuleGroupService ruleGroupService;
    private RuleGroupRepository ruleGroupRepository;

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
                                  RuleGroupDTO ruleGroupDTO, @ApiIgnore @SortDefault(value = RuleGroup.FIELD_GROUP_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleGroupDTO.setTenantId(tenantId);
        Page<RuleGroupDTO> list = ruleGroupRepository.pageAndSortDTO(pageRequest, ruleGroupDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则分组表列表（不分页）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  RuleGroup ruleGroup) {
        ruleGroup.setTenantId(tenantId);
        return Results.success(ruleGroupService.selectList(ruleGroup));
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupDTO.setTenantId(tenantId);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupRepository.updateDTOWhereTenant(ruleGroupDTO, tenantId);
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
}
