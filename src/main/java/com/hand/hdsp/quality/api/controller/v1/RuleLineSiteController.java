package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import com.hand.hdsp.quality.domain.repository.RuleLineRepository;
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
 * <p>规则校验项表（平台级） 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Api(tags = SwaggerTags.RULE_LINE_SITE)
@RestController("ruleLineSiteController.v1")
@RequestMapping("/v1/rule-lines")
public class RuleLineSiteController extends BaseController {

    private RuleLineRepository ruleLineRepository;

    public RuleLineSiteController(RuleLineRepository ruleLineRepository) {
        this.ruleLineRepository = ruleLineRepository;
    }

    @ApiOperation(value = "规则校验项表列表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/list")
    public ResponseEntity<?> list(RuleLineDTO ruleLineDTO, @ApiIgnore @SortDefault(value = RuleLine.FIELD_RULE_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<RuleLineDTO> list = ruleLineRepository.pageAndSortDTO(pageRequest, ruleLineDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则校验项表明细（平台级）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ruleLineId",
            value = "规则校验项表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{ruleLineId}")
    public ResponseEntity<?> detail(@PathVariable Long ruleLineId) {
        RuleLineDTO ruleLineDTO = ruleLineRepository.selectDTOByPrimaryKey(ruleLineId);
        return Results.success(ruleLineDTO);
    }

    @ApiOperation(value = "创建规则校验项表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RuleLineDTO ruleLineDTO) {
        ruleLineDTO.setTenantId(0L);
        this.validObject(ruleLineDTO);
        ruleLineRepository.insertDTOSelective(ruleLineDTO);
        return Results.success(ruleLineDTO);
    }

    @ApiOperation(value = "修改规则校验项表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<?> update(@RequestBody RuleLine ruleLine) {
        ruleLineRepository.updateByPrimaryKeySelective(ruleLine);
        return Results.success(ruleLine);
    }

    @ApiOperation(value = "删除规则校验项表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody RuleLineDTO ruleLineDTO) {
        ruleLineRepository.deleteByPrimaryKey(ruleLineDTO);
        return Results.success();
    }
}
