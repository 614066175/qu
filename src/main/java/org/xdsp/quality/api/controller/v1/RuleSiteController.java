package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseConstants;
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
 * <p>规则表（平台级） 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Api(tags = SwaggerTags.RULE_SITE)
@RestController("ruleSiteController.v1")
@RequestMapping("/v1/rules")
public class RuleSiteController extends BaseController {

    private final RuleRepository ruleRepository;
    private final RuleService ruleService;

    public RuleSiteController(RuleRepository ruleRepository, RuleService ruleService) {
        this.ruleRepository = ruleRepository;
        this.ruleService = ruleService;
    }

    @ApiOperation(value = "规则表列表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/list")
    public ResponseEntity<?> list(RuleDTO ruleDTO, @ApiIgnore @SortDefault(value = Rule.FIELD_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        if (ruleDTO.getGroupId() != null && ruleDTO.getGroupId() == 0) {
            ruleDTO.setGroupId(null);
        }
        Page<RuleDTO> list = ruleRepository.pageAndSortDTO(pageRequest, ruleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则表明细（平台级）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ruleId",
            value = "规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{ruleId}")
    public ResponseEntity<?> detail(@PathVariable Long ruleId) {
        RuleDTO ruleDTO = ruleService.detail(ruleId, BaseConstants.DEFAULT_TENANT_ID, HdspConstant.DEFAULT_PROJECT_ID);
        return Results.success(ruleDTO);
    }

    @ApiOperation(value = "创建规则表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RuleDTO ruleDTO) {
        ruleDTO.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        this.validObject(ruleDTO);
        ruleService.insert(ruleDTO);
        return Results.success(ruleDTO);
    }

    @ApiOperation(value = "修改规则表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<?> update(@RequestBody RuleDTO ruleDTO) {
        ruleDTO.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        ruleService.updateSite(ruleDTO);
        return Results.success(ruleDTO);
    }

    @ApiOperation(value = "删除规则表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody RuleDTO ruleDTO) {
        ruleService.delete(ruleDTO);
        return Results.success();
    }
}
