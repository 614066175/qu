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
import org.xdsp.quality.api.dto.StreamingResultDTO;
import org.xdsp.quality.api.dto.TimeRangeDTO;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.StreamingResult;
import org.xdsp.quality.domain.repository.StreamingResultRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>实时数据方案结果表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Api(tags = SwaggerTags.STREAMING_RESULT)
@RestController("streamingResultController.v1")
@RequestMapping("/v1/{organizationId}/streaming-results")
public class StreamingResultController extends BaseController {

    private final StreamingResultRepository streamingResultRepository;

    public StreamingResultController(StreamingResultRepository streamingResultRepository) {
        this.streamingResultRepository = streamingResultRepository;
    }

    @ApiOperation(value = "根据分组查看对应的实时数据评估方案")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<?> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                     StreamingResultDTO streamingResultDTO,
                                     @SortDefault(value = StreamingResult.FIELD_END_DATE,
                                             direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingResultDTO.setTenantId(tenantId);
        streamingResultDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.listAll(streamingResultDTO, pageRequest));
    }

    @ApiOperation(value = "查看评估结果头")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/result-head")
    public ResponseEntity<?> resultHead(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        StreamingResultDTO streamingResultDTO) {
        streamingResultDTO.setTenantId(tenantId);
        streamingResultDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.showResultHead(streamingResultDTO));
    }

    @ApiOperation(value = "根据分组查看对应的实时数据评估方案执行记录")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/history")
    public ResponseEntity<?> listHistory(@PathVariable(name = "organizationId") Long tenantId,
                                         StreamingResultDTO streamingResultDTO,
                                         @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         @SortDefault(value = StreamingResult.FIELD_START_DATE,
                                                 direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingResultDTO.setTenantId(tenantId);
        streamingResultDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.listHistory(streamingResultDTO, pageRequest));
    }

    @ApiOperation(value = "查看质量分数，规则总数，异常规则数")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/number-view")
    public ResponseEntity<?> numberView(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.numberView(timeRangeDTO));
    }

    @ApiOperation(value = "数据质量分数走势")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/mark-trend")
    public ResponseEntity<?> markTrend(@PathVariable(name = "organizationId") Long tenantId,
                                       @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                       TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.markTrend(timeRangeDTO));
    }

    @ApiOperation(value = "每日不同告警等级数")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/warning-trend")
    public ResponseEntity<?> warningTrend(@PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.warningTrend(timeRangeDTO));
    }

    @ApiOperation(value = "主要延迟topic")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/delay-topic")
    public ResponseEntity<?> delayTopic(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.delayTopicInfo(timeRangeDTO));
    }

    @ApiOperation(value = "展示不同类型异常数")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/rule-error-trend")
    public ResponseEntity<?> ruleErrorTrend(@PathVariable(name = "organizationId") Long tenantId,
                                            @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                            TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(streamingResultRepository.ruleErrorTrend(timeRangeDTO));
    }

    @ApiOperation(value = "实时数据方案结果表列表")
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
                                  StreamingResultDTO streamingResultDTO, @ApiIgnore @SortDefault(value = StreamingResult.FIELD_RESULT_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        streamingResultDTO.setTenantId(tenantId);
        streamingResultDTO.setProjectId(projectId);
        Page<StreamingResultDTO> list = streamingResultRepository.pageAndSortDTO(pageRequest, streamingResultDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "实时数据方案结果表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultId",
            value = "实时数据方案结果表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultId}")
    public ResponseEntity<?> detail(@PathVariable Long resultId) {
        StreamingResultDTO streamingResultDTO = streamingResultRepository.selectDTOByPrimaryKeyAndTenant(resultId);
        return Results.success(streamingResultDTO);
    }

    @ApiOperation(value = "创建实时数据方案结果表")
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
                                    @RequestBody StreamingResultDTO streamingResultDTO) {
        streamingResultDTO.setTenantId(tenantId);
        streamingResultDTO.setProjectId(projectId);
        this.validObject(streamingResultDTO);
        streamingResultRepository.insertDTOSelective(streamingResultDTO);
        return Results.success(streamingResultDTO);
    }

    @ApiOperation(value = "修改实时数据方案结果表")
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
                                    @RequestBody StreamingResultDTO streamingResultDTO) {
        streamingResultDTO.setProjectId(projectId);
        streamingResultRepository.updateDTOAllColumnWhereTenant(streamingResultDTO, tenantId);
        return Results.success(streamingResultDTO);
    }

    @ApiOperation(value = "删除实时数据方案结果表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StreamingResultDTO streamingResultDTO) {
        streamingResultDTO.setTenantId(tenantId);
        streamingResultRepository.deleteByPrimaryKey(streamingResultDTO);
        return Results.success();
    }
}
