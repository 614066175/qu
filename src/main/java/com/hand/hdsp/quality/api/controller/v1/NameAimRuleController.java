package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameAimRule;
import com.hand.hdsp.quality.api.dto.NameAimRuleDTO;
import com.hand.hdsp.quality.domain.repository.NameAimRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>命名标准排除规则表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameAimRuleController.v1")
@RequestMapping("/v1/{organizationId}/name-aim-rules")
public class NameAimRuleController extends BaseController {

    private NameAimRuleRepository nameAimRuleRepository;

    public NameAimRuleController(NameAimRuleRepository nameAimRuleRepository) {
        this.nameAimRuleRepository = nameAimRuleRepository;
    }

    @ApiOperation(value = "命名标准排除规则表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameAimRuleDTO nameAimRuleDTO, @ApiIgnore @SortDefault(value = NameAimRule.FIELD_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameAimRuleDTO.setTenantId(tenantId);
        Page<NameAimRuleDTO> list = nameAimRuleRepository.pageAndSortDTO(pageRequest, nameAimRuleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准排除规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "ruleId",
            value = "命名标准排除规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{ruleId}")
    public ResponseEntity<?> detail(@PathVariable Long ruleId) {
        NameAimRuleDTO nameAimRuleDTO = nameAimRuleRepository.selectDTOByPrimaryKeyAndTenant(ruleId);
        return Results.success(nameAimRuleDTO);
    }

    @ApiOperation(value = "创建命名标准排除规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameAimRuleDTO nameAimRuleDTO) {
        nameAimRuleDTO.setTenantId(tenantId);
        this.validObject(nameAimRuleDTO);
        nameAimRuleRepository.insertDTOSelective(nameAimRuleDTO);
        return Results.success(nameAimRuleDTO);
    }

    @ApiOperation(value = "修改命名标准排除规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameAimRuleDTO nameAimRuleDTO) {
                nameAimRuleRepository.updateDTOWhereTenant(nameAimRuleDTO, tenantId);
        return Results.success(nameAimRuleDTO);
    }

    @ApiOperation(value = "删除命名标准排除规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameAimRuleDTO nameAimRuleDTO) {
                nameAimRuleDTO.setTenantId(tenantId);
        nameAimRuleRepository.deleteByPrimaryKey(nameAimRuleDTO);
        return Results.success();
    }
}
