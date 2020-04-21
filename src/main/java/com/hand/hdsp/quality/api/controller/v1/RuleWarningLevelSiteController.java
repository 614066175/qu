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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>规则告警等级表（平台级） 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Api(tags = SwaggerTags.RULE_WARNING_LEVEL_SITE)
@RestController("ruleWarningLevelSiteController.v1")
@RequestMapping("/v1/rule-warning-levels")
public class RuleWarningLevelSiteController extends BaseController {

    private RuleWarningLevelRepository ruleWarningLevelRepository;

    public RuleWarningLevelSiteController(RuleWarningLevelRepository ruleWarningLevelRepository) {
        this.ruleWarningLevelRepository = ruleWarningLevelRepository;
    }

    @ApiOperation(value = "规则告警等级表列表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<?> list(RuleWarningLevelDTO ruleWarningLevelDTO, @ApiIgnore @SortDefault(value = RuleWarningLevel.FIELD_LEVEL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<RuleWarningLevelDTO> list = ruleWarningLevelRepository.pageAndSortDTO(pageRequest, ruleWarningLevelDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则告警等级表明细（平台级）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "levelId",
            value = "规则告警等级表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{levelId}")
    public ResponseEntity<?> detail(@PathVariable Long levelId) {
        RuleWarningLevelDTO ruleWarningLevelDTO = ruleWarningLevelRepository.selectDTOByPrimaryKey(levelId);
        return Results.success(ruleWarningLevelDTO);
    }

    @ApiOperation(value = "创建规则告警等级表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RuleWarningLevelDTO ruleWarningLevelDTO) {
        ruleWarningLevelDTO.setTenantId(0L);
        this.validObject(ruleWarningLevelDTO);
        ruleWarningLevelRepository.insertDTOSelective(ruleWarningLevelDTO);
        return Results.success(ruleWarningLevelDTO);
    }

    @ApiOperation(value = "修改规则告警等级表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<?> update(@RequestBody RuleWarningLevel ruleWarningLevel) {
        ruleWarningLevelRepository.updateByPrimaryKeySelective(ruleWarningLevel);
        return Results.success(ruleWarningLevel);
    }

    @ApiOperation(value = "删除规则告警等级表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody RuleWarningLevelDTO ruleWarningLevelDTO) {
        ruleWarningLevelRepository.deleteByPrimaryKey(ruleWarningLevelDTO);
        return Results.success();
    }
}
