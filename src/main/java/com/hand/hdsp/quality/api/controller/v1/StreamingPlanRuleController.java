package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.StreamingPlanRuleDTO;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.StreamingPlanRule;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRuleRepository;
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
 * <p>实时数据方案-规则表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Api(tags = SwaggerTags.STREAMING_PLAN_RULE)
@RestController("streamingPlanRuleController.v1")
@RequestMapping("/v1/{organizationId}/streaming-plan-rules")
public class StreamingPlanRuleController extends BaseController {

    private final StreamingPlanRuleRepository streamingPlanRuleRepository;

    public StreamingPlanRuleController(StreamingPlanRuleRepository streamingPlanRuleRepository) {
        this.streamingPlanRuleRepository = streamingPlanRuleRepository;
    }

    @ApiOperation(value = "实时数据方案-规则表列表")
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
                                  StreamingPlanRuleDTO streamingPlanRuleDTO, @ApiIgnore @SortDefault(value = StreamingPlanRule.FIELD_PLAN_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingPlanRuleDTO.setTenantId(tenantId);
        streamingPlanRuleDTO.setProjectId(projectId);
        Page<StreamingPlanRuleDTO> list = streamingPlanRuleRepository.pageAndSortDTO(pageRequest, streamingPlanRuleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "实时数据方案-规则表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planRuleId",
            value = "实时数据方案-规则表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planRuleId}")
    public ResponseEntity<?> detail(@PathVariable Long planRuleId) {
        StreamingPlanRuleDTO streamingPlanRuleDTO = streamingPlanRuleRepository.selectDTOByPrimaryKeyAndTenant(planRuleId);
        return Results.success(streamingPlanRuleDTO);
    }

    @ApiOperation(value = "创建实时数据方案-规则表")
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
                                    @RequestBody StreamingPlanRuleDTO streamingPlanRuleDTO) {
        streamingPlanRuleDTO.setTenantId(tenantId);
        streamingPlanRuleDTO.setProjectId(projectId);
        this.validObject(streamingPlanRuleDTO);
        streamingPlanRuleRepository.insertDTOSelective(streamingPlanRuleDTO);
        return Results.success(streamingPlanRuleDTO);
    }

    @ApiOperation(value = "修改实时数据方案-规则表")
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
                                    @RequestBody StreamingPlanRuleDTO streamingPlanRuleDTO) {
        streamingPlanRuleDTO.setProjectId(projectId);
        streamingPlanRuleRepository.updateDTOAllColumnWhereTenant(streamingPlanRuleDTO, tenantId);
        return Results.success(streamingPlanRuleDTO);
    }

    @ApiOperation(value = "删除实时数据方案-规则表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StreamingPlanRuleDTO streamingPlanRuleDTO) {
        streamingPlanRuleDTO.setTenantId(tenantId);
        streamingPlanRuleRepository.deleteByPrimaryKey(streamingPlanRuleDTO);
        return Results.success();
    }
}
