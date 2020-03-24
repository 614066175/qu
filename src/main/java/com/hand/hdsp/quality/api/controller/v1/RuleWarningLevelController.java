package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;
import com.hand.hdsp.quality.domain.repository.RuleWarningLevelRepository;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>规则告警等级表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Api(tags = SwaggerTags.RULE_WARNING_LEVEL)
@RestController("ruleWarningLevelController.v1")
@RequestMapping("/v1/{organizationId}/rule-warning-levels")
public class RuleWarningLevelController extends BaseController {

    private RuleWarningLevelRepository ruleWarningLevelRepository;

    public RuleWarningLevelController(RuleWarningLevelRepository ruleWarningLevelRepository) {
        this.ruleWarningLevelRepository = ruleWarningLevelRepository;
    }

    @ApiOperation(value = "规则告警等级表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  RuleWarningLevelDTO ruleWarningLevelDTO, @ApiIgnore @SortDefault(value = RuleWarningLevel.FIELD_LEVEL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleWarningLevelDTO.setTenantId(tenantId);
        Page<RuleWarningLevelDTO> list = ruleWarningLevelRepository.pageAndSortDTO(pageRequest, ruleWarningLevelDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则告警等级表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "levelId",
            value = "规则告警等级表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{levelId}")
    public ResponseEntity<?> detail(@PathVariable Long levelId) {
        RuleWarningLevelDTO ruleWarningLevelDTO = ruleWarningLevelRepository.selectDTOByPrimaryKeyAndTenant(levelId);
        return Results.success(ruleWarningLevelDTO);
    }

    @ApiOperation(value = "创建规则告警等级表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody RuleWarningLevelDTO ruleWarningLevelDTO) {
        ruleWarningLevelDTO.setTenantId(tenantId);
        this.validObject(ruleWarningLevelDTO);
        ruleWarningLevelRepository.insertDTOSelective(ruleWarningLevelDTO);
        return Results.success(ruleWarningLevelDTO);
    }

    @ApiOperation(value = "修改规则告警等级表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody RuleWarningLevelDTO ruleWarningLevelDTO) {
        ruleWarningLevelRepository.updateDTOWhereTenant(ruleWarningLevelDTO, tenantId);
        return Results.success(ruleWarningLevelDTO);
    }

    @ApiOperation(value = "删除规则告警等级表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody RuleWarningLevelDTO ruleWarningLevelDTO) {
        ruleWarningLevelDTO.setTenantId(tenantId);
        ruleWarningLevelRepository.deleteByPrimaryKey(ruleWarningLevelDTO);
        return Results.success();
    }
}
