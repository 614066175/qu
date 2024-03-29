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
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.StreamingPlanDTO;
import org.xdsp.quality.app.service.StreamingPlanService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.StreamingPlan;
import org.xdsp.quality.domain.repository.StreamingPlanRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>实时数据评估方案表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Api(tags = SwaggerTags.STREAMING_PLAN)
@RestController("streamingPlanController.v1")
@RequestMapping("/v1/{organizationId}/streaming-plans")
public class StreamingPlanController extends BaseController {

    private final StreamingPlanRepository streamingPlanRepository;
    private final StreamingPlanService streamingPlanService;

    public StreamingPlanController(StreamingPlanRepository streamingPlanRepository,
                                   StreamingPlanService streamingPlanService) {
        this.streamingPlanRepository = streamingPlanRepository;
        this.streamingPlanService = streamingPlanService;
    }

    @ApiOperation(value = "实时数据评估方案表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  StreamingPlanDTO streamingPlanDTO, @ApiIgnore @SortDefault(value = StreamingPlan.FIELD_PLAN_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingPlanDTO.setTenantId(tenantId);
        streamingPlanDTO.setProjectId(projectId);
        Page<StreamingPlanDTO> list = streamingPlanRepository.pageAndSortDTO(pageRequest, streamingPlanDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "实时数据评估方案表列表（不分页）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listWithOutPage(@PathVariable(name = "organizationId") Long tenantId,
                                             @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                             StreamingPlan streamingPlan) {
        streamingPlan.setTenantId(tenantId);
        streamingPlan.setProjectId(projectId);
        return Results.success(streamingPlanRepository.select(streamingPlan));
    }

    @ApiOperation(value = "根据方案名找到对应分组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/group")
    public ResponseEntity<?> group(@PathVariable(name = "organizationId") Long tenantId,
                                   @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                   StreamingPlanDTO streamingPlanDTO) {
        streamingPlanDTO.setTenantId(tenantId);
        streamingPlanDTO.setProjectId(projectId);
        return Results.success(streamingPlanRepository.getGroupByPlanName(streamingPlanDTO));
    }


    @ApiOperation(value = "实时数据评估方案表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "planId",
            value = "实时数据评估方案表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{planId}")
    public ResponseEntity<?> detail(@PathVariable Long planId) {
        StreamingPlanDTO streamingPlanDTO = streamingPlanRepository.selectDTOByPrimaryKeyAndTenant(planId);
        return Results.success(streamingPlanDTO);
    }

    @ApiOperation(value = "创建实时数据评估方案表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody StreamingPlanDTO streamingPlanDTO) {
        streamingPlanDTO.setTenantId(tenantId);
        streamingPlanDTO.setProjectId(projectId);
        this.validObject(streamingPlanDTO);
        streamingPlanRepository.insertDTOSelective(streamingPlanDTO);
        return Results.success(streamingPlanDTO);
    }

    @ApiOperation(value = "修改实时数据评估方案表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody StreamingPlanDTO streamingPlanDTO) {
        streamingPlanDTO.setProjectId(projectId);
        streamingPlanRepository.updateDTOAllColumnWhereTenant(streamingPlanDTO, tenantId);
        return Results.success(streamingPlanDTO);
    }

    @ApiOperation(value = "删除实时数据评估方案表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StreamingPlanDTO streamingPlanDTO) {
        streamingPlanDTO.setTenantId(tenantId);
        streamingPlanService.delete(streamingPlanDTO);
        return Results.success();
    }
}
