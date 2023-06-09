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
import org.xdsp.quality.api.dto.StreamingResultRuleDTO;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.StreamingResultRule;
import org.xdsp.quality.domain.repository.StreamingResultRuleRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>实时数据方案结果表-规则信息 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Api(tags = SwaggerTags.STREAMING_RESULT_RULE)
@RestController("streamingResultRuleController.v1")
@RequestMapping("/v1/{organizationId}/streaming-result-rules")
public class StreamingResultRuleController extends BaseController {

    private final StreamingResultRuleRepository streamingResultRuleRepository;

    public StreamingResultRuleController(StreamingResultRuleRepository streamingResultRuleRepository) {
        this.streamingResultRuleRepository = streamingResultRuleRepository;
    }

    @ApiOperation(value = "实时数据方案结果表-规则信息列表")
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
                                  StreamingResultRuleDTO streamingResultRuleDTO, @ApiIgnore @SortDefault(value = StreamingResultRule.FIELD_RESULT_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingResultRuleDTO.setTenantId(tenantId);
        streamingResultRuleDTO.setProjectId(projectId);
        Page<StreamingResultRuleDTO> list = streamingResultRuleRepository.pageAndSortDTO(pageRequest, streamingResultRuleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "评估结果规则错误信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/result-rule")
    public ResponseEntity<?> resultRule(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        StreamingResultRuleDTO streamingResultRuleDTO) {
        streamingResultRuleDTO.setTenantId(tenantId);
        streamingResultRuleDTO.setProjectId(projectId);
        return Results.success(streamingResultRuleRepository.listResultRule(streamingResultRuleDTO));
    }

    @ApiOperation(value = "实时数据方案结果表-规则信息明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultRuleId",
            value = "实时数据方案结果表-规则信息主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultRuleId}")
    public ResponseEntity<?> detail(@PathVariable Long resultRuleId) {
        StreamingResultRuleDTO streamingResultRuleDTO = streamingResultRuleRepository.selectDTOByPrimaryKeyAndTenant(resultRuleId);
        return Results.success(streamingResultRuleDTO);
    }

    @ApiOperation(value = "创建实时数据方案结果表-规则信息")
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
                                    @RequestBody StreamingResultRuleDTO streamingResultRuleDTO) {
        streamingResultRuleDTO.setTenantId(tenantId);
        streamingResultRuleDTO.setProjectId(projectId);
        this.validObject(streamingResultRuleDTO);
        streamingResultRuleRepository.insertDTOSelective(streamingResultRuleDTO);
        return Results.success(streamingResultRuleDTO);
    }

    @ApiOperation(value = "修改实时数据方案结果表-规则信息")
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
                                    @RequestBody StreamingResultRuleDTO streamingResultRuleDTO) {
        streamingResultRuleDTO.setProjectId(projectId);
        streamingResultRuleRepository.updateDTOAllColumnWhereTenant(streamingResultRuleDTO, tenantId);
        return Results.success(streamingResultRuleDTO);
    }

    @ApiOperation(value = "删除实时数据方案结果表-规则信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StreamingResultRuleDTO streamingResultRuleDTO) {
        streamingResultRuleDTO.setTenantId(tenantId);
        streamingResultRuleRepository.deleteByPrimaryKey(streamingResultRuleDTO);
        return Results.success();
    }
}
