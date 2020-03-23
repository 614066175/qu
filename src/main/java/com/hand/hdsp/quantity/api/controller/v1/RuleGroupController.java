package com.hand.hdsp.quantity.api.controller.v1;

import com.hand.hdsp.quantity.api.dto.RuleGroupDTO;
import com.hand.hdsp.quantity.app.service.RuleGroupService;
import com.hand.hdsp.quantity.config.SwaggerTags;
import com.hand.hdsp.quantity.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quantity.infra.validator.groups.Create;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/v1/{organizationId}/rule-group")
public class RuleGroupController extends BaseController {

    @Autowired
    private RuleGroupService ruleGroupService;

    @Autowired
    private RuleGroupRepository ruleGroupRepository;

    @ApiOperation(value = "创建规则分组表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity create(@PathVariable("organizationId") Long tenantId, @RequestBody RuleGroupDTO ruleGroupDTO) {
        ruleGroupDTO.setTenantId(tenantId);
        validObject(ruleGroupDTO, Create.class);
        ruleGroupService.create(ruleGroupDTO);
        return Results.success();
    }
}
