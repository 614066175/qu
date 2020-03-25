package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.app.service.RuleService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
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
 * <p>规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Api(tags = SwaggerTags.RULE)
@RestController("ruleController.v1")
@RequestMapping("/v1/{organizationId}/rules")
public class RuleController extends BaseController {

    private RuleRepository ruleRepository;
    private RuleService ruleService;

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
                                  RuleDTO ruleDTO, @ApiIgnore @SortDefault(value = Rule.FIELD_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleDTO.setTenantId(tenantId);
        Page<RuleDTO> list = ruleRepository.pageAndSortDTO(pageRequest, ruleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则表明细")
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
    @GetMapping("/{ruleId}")
    public ResponseEntity<?> detail(@PathVariable Long ruleId) {
        RuleDTO ruleDTO = ruleService.detail(ruleId);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody RuleDTO ruleDTO) {
        ruleDTO.setTenantId(tenantId);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody RuleDTO ruleDTO) {
        ruleDTO.setTenantId(tenantId);
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
