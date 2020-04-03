package com.hand.hdsp.quality.api.controller.v1;

import java.util.Date;

import com.hand.hdsp.quality.api.dto.BatchResultDTO;
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
                                     BatchResultDTO batchResultDTO,
                                     PageRequest pageRequest){
        batchResultDTO.setTenantId(tenantId);
        return Results.success(batchResultRepository.listAll(batchResultDTO,pageRequest));
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
                                         PageRequest pageRequest) {
        batchResultDTO.setTenantId(tenantId);
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
                                        BatchResultDTO batchResultDTO){
        batchResultDTO.setTenantId(tenantId);
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
                                        String timeRange,
                                        Date startDate,
                                        Date endDate){
        return Results.success(batchResultRepository.numberView(tenantId, timeRange, startDate, endDate));
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
                                                 String timeRange,
                                                 Date startDate,
                                                 Date endDate){
        return Results.success(batchResultRepository.checkTypePercentage(tenantId, timeRange, startDate, endDate));
    }

    @ApiOperation(value = "主要可改进指标（规则）")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/rule-percentage")
    public ResponseEntity<?> rulePercentage(@PathVariable(name = "organizationId") Long tenantId,
                                            String timeRange,
                                            Date startDate,
                                            Date endDate,
                                            String rule){
        return Results.success(batchResultRepository.rulePercentage(tenantId, timeRange, startDate, endDate, rule));
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
                                       String timeRange,
                                       Date startDate,
                                       Date endDate){
        return Results.success(batchResultRepository.markTrend(tenantId, timeRange, startDate, endDate));
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
                                           String timeRange,
                                           Date startDate,
                                           Date endDate){
        return Results.success(batchResultRepository.daysErrorRule(tenantId, timeRange, startDate, endDate));
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
                                          String timeRange,
                                          Date startDate,
                                          Date endDate){
        return Results.success(batchResultRepository.warningTrend(tenantId, timeRange, startDate, endDate));
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
                                     @RequestParam("jobId") String jobId){
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
                                  BatchResultDTO batchResultDTO, @ApiIgnore @SortDefault(value = BatchResult.FIELD_RESULT_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultDTO.setTenantId(tenantId);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultDTO batchResultDTO) {
        batchResultDTO.setTenantId(tenantId);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody BatchResultDTO batchResultDTO) {
        batchResultRepository.updateDTOWhereTenant(batchResultDTO, tenantId);
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
}
