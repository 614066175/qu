package com.hand.hdsp.quality.api.controller.v1;


import java.util.Map;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.api.dto.ExceptionDataDTO;
import com.hand.hdsp.quality.api.dto.TimeRangeDTO;
import com.hand.hdsp.quality.app.service.BatchResultService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>批数据方案结果表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Api(tags = SwaggerTags.BATCH_RESULT)
@RestController("batchResultController.v1")
@RequestMapping("/v1/{organizationId}/batch-results")
public class BatchResultController extends BaseController {

    private final BatchResultRepository batchResultRepository;
    private final BatchResultService batchResultService;

    public BatchResultController(BatchResultRepository batchResultRepository, BatchResultService batchResultService) {
        this.batchResultRepository = batchResultRepository;
        this.batchResultService = batchResultService;
    }

    @ApiOperation(value = "根据分组查看对应的批数据评估方案")
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
                                     BatchResultDTO batchResultDTO,
                                     @SortDefault(value = BatchResult.FIELD_START_DATE,
                                             direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultDTO.setTenantId(tenantId);
        batchResultDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.listAll(batchResultDTO, pageRequest));
    }

    @ApiOperation(value = "根据分组查看对应的批数据评估方案执行记录")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/history")
    public ResponseEntity<?> listHistory(@PathVariable(name = "organizationId") Long tenantId,
                                         BatchResultDTO batchResultDTO,
                                         @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         @SortDefault(value = BatchResult.FIELD_START_DATE,
                                                 direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultDTO.setTenantId(tenantId);
        batchResultDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.listHistory(batchResultDTO, pageRequest));
    }

    @ApiOperation(value = "查看评估报告结果头")
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
                                        BatchResultDTO batchResultDTO) {
        batchResultDTO.setTenantId(tenantId);
        batchResultDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.showResultHead(batchResultDTO));
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
        return Results.success(batchResultRepository.numberView(timeRangeDTO));
    }


    @ApiOperation(value = "规则列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/rule-list")
    public ResponseEntity<?> ruleList(@PathVariable(name = "organizationId") Long tenantId,
                                      @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                      TimeRangeDTO timeRangeDTO,
                                      PageRequest pageRequest) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.ruleList(pageRequest, timeRangeDTO));
    }

    @ApiOperation(value = "校验项列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/item-list")
    public ResponseEntity<?> itemList(@PathVariable(name = "organizationId") Long tenantId,
                                      @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                      TimeRangeDTO timeRangeDTO,
                                      PageRequest pageRequest) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.itemList(pageRequest, timeRangeDTO));
    }

    @ApiOperation(value = "告警列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/error-rule-list")
    @ProcessLovValue(targetField = {"body", "body.warningLevelList"})
    public ResponseEntity<?> errorRuleList(@PathVariable(name = "organizationId") Long tenantId,
                                           @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                           TimeRangeDTO timeRangeDTO,
                                           PageRequest pageRequest) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.errorRuleList(pageRequest, timeRangeDTO));
    }

    @ApiOperation(value = "数据质量评估统计")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/check-type-percentage")
    public ResponseEntity<?> checkTypePercentage(@PathVariable(name = "organizationId") Long tenantId,
                                                 @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                 TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.checkTypePercentage(timeRangeDTO));
    }

    @ApiOperation(value = "灾区表占比情况")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/error-table-percentage")
    public ResponseEntity<?> errorTablePercentage(@PathVariable(name = "organizationId") Long tenantId,
                                                  @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                  TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.errorTablePercentage(timeRangeDTO));
    }

    @ApiOperation(value = "灾区表-校验项占比情况")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/error-table-item-percentage")
    @ProcessLovValue(targetField = {"body"})
    public ResponseEntity<?> errorTableItemPercentage(@PathVariable(name = "organizationId") Long tenantId,
                                                      @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                      TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.errorTableItemPercentage(timeRangeDTO));
    }


    @ApiOperation(value = "灾区表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/error-table-list")
    public ResponseEntity<?> errorTableList(@PathVariable(name = "organizationId") Long tenantId,
                                            TimeRangeDTO timeRangeDTO,
                                            @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                            PageRequest pageRequest) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.errorTableList(pageRequest, timeRangeDTO));
    }

    @ApiOperation(value = "灾区表-校验项列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/error-table-item-list")
    @ProcessLovValue(targetField = {"body", "body.warningLevelList"})
    public ResponseEntity<?> errorTableItemList(@PathVariable(name = "organizationId") Long tenantId,
                                                @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                TimeRangeDTO timeRangeDTO,
                                                PageRequest pageRequest) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.errorTableItemList(pageRequest, timeRangeDTO));
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
        return Results.success(batchResultRepository.markTrend(timeRangeDTO));
    }

    @ApiOperation(value = "表级，表间，字段级异常规则数")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/error-rule-trend")
    public ResponseEntity<?> daysErrorRule(@PathVariable(name = "organizationId") Long tenantId,
                                           @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                           TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.daysErrorRule(timeRangeDTO));
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
        return Results.success(batchResultRepository.warningTrend(timeRangeDTO));
    }

    @ApiOperation(value = "查看运行日志")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/log")
    public ResponseEntity<?> showLog(@PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam("execId") int execId,
                                     @RequestParam("jobId") String jobId) {
        return Results.success(batchResultService.showLog(tenantId, execId, jobId));
    }

    @ApiOperation(value = "批数据方案结果表列表")
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
                                  BatchResultDTO batchResultDTO, @ApiIgnore @SortDefault(value = BatchResult.FIELD_RESULT_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultDTO.setTenantId(tenantId);
        batchResultDTO.setProjectId(projectId);
        Page<BatchResultDTO> list = batchResultRepository.pageAndSortDTO(pageRequest, batchResultDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案结果表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultId",
            value = "批数据方案结果表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultId}")
    public ResponseEntity<?> detail(@PathVariable Long resultId) {
        BatchResultDTO batchResultDTO = batchResultRepository.selectDTOByPrimaryKeyAndTenant(resultId);
        return Results.success(batchResultDTO);
    }

    @ApiOperation(value = "创建批数据方案结果表")
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
                                    @RequestBody BatchResultDTO batchResultDTO) {
        batchResultDTO.setTenantId(tenantId);
        batchResultDTO.setProjectId(projectId);
        this.validObject(batchResultDTO);
        batchResultRepository.insertDTOSelective(batchResultDTO);
        return Results.success(batchResultDTO);
    }

    @ApiOperation(value = "修改批数据方案结果表")
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
                                    @RequestBody BatchResultDTO batchResultDTO) {
        batchResultDTO.setProjectId(projectId);
        batchResultRepository.updateDTOAllColumnWhereTenant(batchResultDTO, tenantId);
        return Results.success(batchResultDTO);
    }

    @ApiOperation(value = "删除批数据方案结果表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchResultDTO batchResultDTO) {
        batchResultDTO.setTenantId(tenantId);
        batchResultRepository.deleteByPrimaryKey(batchResultDTO);
        return Results.success();
    }

    @ApiOperation(value = "查看评估方案评估信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/result-detail")
    public ResponseEntity<BatchResultDTO> resultDetail(@PathVariable(name = "organizationId") Long tenantId,
                                                       @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                       BatchResultDTO batchResultDTO) {
        batchResultDTO.setTenantId(tenantId);
        batchResultDTO.setProjectId(projectId);
        return Results.success(batchResultService.listResultDetail(batchResultDTO));
    }


    @ApiOperation(value = "评估详情-异常数据头查询")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/exception-detail-head/{resultId}")
    public ResponseEntity<?> exceptionDetailHead(@PathVariable(name = "organizationId") Long tenantId,
                                                 @PathVariable Long resultId,
                                                 PageRequest pageRequest) {
        return Results.success(batchResultService.listExceptionDetailHead(resultId, tenantId, pageRequest));
    }


    @ApiOperation(value = "评估详情-异常数据")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/exception-detail")
    public ResponseEntity<Page<Map<String, Object>>> exceptionDetail(@PathVariable(name = "organizationId") Long tenantId,
                                                                     ExceptionDataDTO exceptionDataDTO,
                                                                     PageRequest pageRequest) {
        exceptionDataDTO.setTenantId(tenantId);
        return Results.success(batchResultService.listExceptionDetail(exceptionDataDTO, pageRequest));
    }

    @ApiOperation(value = "问题数据趋势分析")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/problem_trend")
    public ResponseEntity<?> problemTrend(@PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultService.listProblemData(timeRangeDTO));
    }

    @ApiOperation(value = "问题触发次数统计")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/problem-trigger")
    public ResponseEntity<?> problemTrigger(@PathVariable(name = "organizationId") Long tenantId,
                                            @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                            TimeRangeDTO timeRangeDTO) {
        timeRangeDTO.setTenantId(tenantId);
        timeRangeDTO.setProjectId(projectId);
        return Results.success(batchResultRepository.problemTrigger(timeRangeDTO));
    }
}
