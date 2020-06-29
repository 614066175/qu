package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.app.service.StreamingPlanBaseService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.StreamingPlanBase;
import com.hand.hdsp.quality.domain.repository.StreamingPlanBaseRepository;
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
 * <p>实时数据方案-基础配置表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Api(tags = SwaggerTags.STREAMING_PLAN_BASE)
@RestController("streamingPlanBaseController.v1")
@RequestMapping("/v1/{organizationId}/streaming-plan-bases")
public class StreamingPlanBaseController extends BaseController {

    private final StreamingPlanBaseRepository streamingPlanBaseRepository;
    private final StreamingPlanBaseService streamingPlanBaseService;

    public StreamingPlanBaseController(StreamingPlanBaseRepository streamingPlanBaseRepository,
                                       StreamingPlanBaseService streamingPlanBaseService) {
        this.streamingPlanBaseRepository = streamingPlanBaseRepository;
        this.streamingPlanBaseService = streamingPlanBaseService;
    }

    @ApiOperation(value = "实时数据方案-基础配置表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  StreamingPlanBaseDTO streamingPlanBaseDTO, @ApiIgnore @SortDefault(value = StreamingPlanBase.FIELD_PLAN_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingPlanBaseDTO.setTenantId(tenantId);
        Page<StreamingPlanBaseDTO> list = streamingPlanBaseRepository.pageAndSortDTO(pageRequest, streamingPlanBaseDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "实时数据方案-基础配置表列表（含规则计数）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listBase(@PathVariable(name = "organizationId") Long tenantId,
                                      StreamingPlanBaseDTO streamingPlanBaseDTO, @ApiIgnore @SortDefault(value = StreamingPlanBase.FIELD_PLAN_BASE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingPlanBaseDTO.setTenantId(tenantId);
        Page<StreamingPlanBaseDTO> list = streamingPlanBaseRepository.list(pageRequest, streamingPlanBaseDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "实时数据方案-基础配置表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planBaseId",
            value = "实时数据方案-基础配置表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planBaseId}")
    public ResponseEntity<?> detail(@PathVariable Long planBaseId) {
        StreamingPlanBaseDTO streamingPlanBaseDTO = streamingPlanBaseService.detail(planBaseId);
        return Results.success(streamingPlanBaseDTO);
    }

    @ApiOperation(value = "创建实时数据方案-基础配置表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody StreamingPlanBaseDTO streamingPlanBaseDTO) {
        streamingPlanBaseDTO.setTenantId(tenantId);
        this.validObject(streamingPlanBaseDTO);
        streamingPlanBaseService.insert(streamingPlanBaseDTO);
        return Results.success(streamingPlanBaseDTO);
    }

    @ApiOperation(value = "修改实时数据方案-基础配置表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StreamingPlanBaseDTO streamingPlanBaseDTO) {
        streamingPlanBaseDTO.setTenantId(tenantId);
        streamingPlanBaseService.update(streamingPlanBaseDTO);
        return Results.success(streamingPlanBaseDTO);
    }

    @ApiOperation(value = "删除实时数据方案-基础配置表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StreamingPlanBaseDTO streamingPlanBaseDTO) {
        streamingPlanBaseDTO.setTenantId(tenantId);
        streamingPlanBaseService.delete(streamingPlanBaseDTO);
        return Results.success();
    }
}
