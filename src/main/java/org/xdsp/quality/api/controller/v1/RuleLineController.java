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
import org.xdsp.quality.api.dto.RuleLineDTO;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.RuleLine;
import org.xdsp.quality.domain.repository.RuleLineRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>规则校验项表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Api(tags = SwaggerTags.RULE_LINE)
@RestController("ruleLineController.v1")
@RequestMapping("/v1/{organizationId}/rule-lines")
public class RuleLineController extends BaseController {

    private final RuleLineRepository ruleLineRepository;

    public RuleLineController(RuleLineRepository ruleLineRepository) {
        this.ruleLineRepository = ruleLineRepository;
    }

    @ApiOperation(value = "规则校验项表列表")
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
                                  RuleLineDTO ruleLineDTO, @ApiIgnore @SortDefault(value = RuleLine.FIELD_RULE_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleLineDTO.setTenantId(tenantId);
        ruleLineDTO.setProjectId(projectId);
        Page<RuleLineDTO> list = ruleLineRepository.pageAndSortDTO(pageRequest, ruleLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则校验项表列表（标准规则租户级）")
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
                                        RuleLineDTO ruleLineDTO, @ApiIgnore @SortDefault(value = RuleLine.FIELD_RULE_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        ruleLineDTO.setTenantId(tenantId);
        ruleLineDTO.setProjectId(projectId);
        Page<RuleLineDTO> list = ruleLineRepository.listTenant(pageRequest, ruleLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则校验项表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "ruleLineId",
            value = "规则校验项表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{ruleLineId}")
    public ResponseEntity<?> detail(@PathVariable Long ruleLineId) {
        RuleLineDTO ruleLineDTO = ruleLineRepository.selectDTOByPrimaryKeyAndTenant(ruleLineId);
        return Results.success(ruleLineDTO);
    }

    @ApiOperation(value = "创建规则校验项表")
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
                                    @RequestBody RuleLineDTO ruleLineDTO) {
        ruleLineDTO.setTenantId(tenantId);
        ruleLineDTO.setProjectId(projectId);
        this.validObject(ruleLineDTO);
        ruleLineRepository.insertDTOSelective(ruleLineDTO);
        return Results.success(ruleLineDTO);
    }

    @ApiOperation(value = "修改规则校验项表")
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
                                    @RequestBody RuleLineDTO ruleLineDTO) {
        ruleLineDTO.setProjectId(projectId);
        ruleLineRepository.updateDTOAllColumnWhereTenant(ruleLineDTO, tenantId);
        return Results.success(ruleLineDTO);
    }

    @ApiOperation(value = "删除规则校验项表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody RuleLineDTO ruleLineDTO) {
        ruleLineDTO.setTenantId(tenantId);
        ruleLineRepository.deleteByPrimaryKey(ruleLineDTO);
        return Results.success();
    }
}
