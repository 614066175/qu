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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 规则分组表（平台级） 管理 API
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 12:03
 */
@Slf4j
@Api(tags = SwaggerTags.RULE_GROUP_SITE)
@RestController("RuleGroupSiteController.v1")
@RequestMapping("/v1/rule-groups")
public class RuleGroupSiteController extends BaseController {

    private final RuleGroupService ruleGroupService;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleGroupSiteController(RuleGroupService ruleGroupService, RuleGroupRepository ruleGroupRepository) {
        this.ruleGroupService = ruleGroupService;
        this.ruleGroupRepository = ruleGroupRepository;
    }

    @ApiOperation(value = "规则分组表列表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<?> list(RuleGroupDTO ruleGroupDTO, @ApiIgnore @SortDefault(value = RuleGroup.FIELD_GROUP_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<RuleGroupDTO> list = ruleGroupRepository.pageAndSortDTO(pageRequest, ruleGroupDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "规则分组表列表（不分页平台级）")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/list")
    public ResponseEntity<?> listNoPage(RuleGroup ruleGroup) {
        return Results.success(ruleGroupService.selectList(ruleGroup));
    }

    @ApiOperation(value = "规则分组表明细（平台级）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "groupId",
            value = "规则分组表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{groupId}")
    public ResponseEntity<?> detail(@PathVariable Long groupId) {
        RuleGroupDTO ruleGroupDTO = ruleGroupRepository.selectDTOByPrimaryKey(groupId);
        return Results.success(ruleGroupDTO);
    }

    @ApiOperation(value = "创建规则分组表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupDTO.setTenantId(0L);
        validObject(ruleGroupDTO);
        ruleGroupService.create(ruleGroupDTO);
//        ruleGroupRepository.insertDTOSelective(ruleGroupDTO);
        return Results.success(ruleGroupDTO);
    }

    @ApiOperation(value = "修改规则分组表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<?> update(@RequestBody RuleGroup ruleGroup) {
        ruleGroupRepository.updateByPrimaryKey(ruleGroup);
        return Results.success(ruleGroup);
    }

    @ApiOperation(value = "删除规则分组表（平台级）")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupService.delete(ruleGroupDTO);
        return Results.success();
    }
}
