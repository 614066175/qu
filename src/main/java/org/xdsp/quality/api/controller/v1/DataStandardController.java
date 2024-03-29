package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.app.service.DataStandardService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.domain.repository.DataStandardRepository;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.export.dto.DataStandardExportDTO;
import org.xdsp.quality.infra.mapper.StandardDocMapper;
import org.xdsp.quality.infra.util.StandardHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.OFFLINE_APPROVING;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.ONLINE_APPROVING;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 11:18
 * @since 1.0
 */
@Api(tags = SwaggerTags.DATA_STANDARD)
@RestController("dataStandardController.v1")
@RequestMapping("/v1/{organizationId}/data-standard")
public class DataStandardController {

    private final DataStandardService dataStandardService;

    private final DataStandardRepository dataStandardRepository;

    private final StandardDocMapper standardDocMapper;

    private final List<StandardHandler> standardHandlers;

    public DataStandardController(DataStandardService dataStandardService, DataStandardRepository dataStandardRepository, StandardDocMapper standardDocMapper, List<StandardHandler> standardHandlers) {
        this.dataStandardService = dataStandardService;
        this.dataStandardRepository = dataStandardRepository;
        this.standardDocMapper = standardDocMapper;
        this.standardHandlers = standardHandlers;
    }


    @ApiOperation(value = "数据标准列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<Page<DataStandardDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                                                      @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                      DataStandardDTO dataStandardDTO, PageRequest pageRequest) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        Page<DataStandardDTO> list = dataStandardService.list(pageRequest, dataStandardDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "数据标准详情")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/{standardId}")
    public ResponseEntity<DataStandardDTO> detail(@PathVariable(name = "organizationId") Long tenantId,
                                                  @PathVariable(name = "standardId") Long standardId) {
        return Results.success(dataStandardService.detail(tenantId, standardId));
    }

    @ApiOperation(value = "数据标准创建")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DataStandardDTO> create(@PathVariable(name = "organizationId") Long tenantId,
                                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                  @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dataStandardService.create(dataStandardDTO);
        return Results.success(dataStandardDTO);
    }

    @ApiOperation(value = "数据标准删除")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<DataStandardDTO> delete(@PathVariable(name = "organizationId") Long tenantId,
                                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                  @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dataStandardService.delete(dataStandardDTO);
        return Results.success(dataStandardDTO);
    }

    @ApiOperation(value = "数据标准修改")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DataStandardDTO> update(@PathVariable(name = "organizationId") Long tenantId,
                                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                  @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dataStandardService.update(dataStandardDTO);
        return Results.success(dataStandardDTO);
    }


    @ApiOperation(value = "数据标准修改状态")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/update-status")
    public ResponseEntity<DataStandardDTO> updateStatus(@PathVariable(name = "organizationId") Long tenantId,
                                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                        @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dataStandardService.updateStatus(dataStandardDTO);
        return Results.success(dataStandardDTO);
    }

    @ApiOperation(value = "发布数据标准")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/publish-off")
    public ResponseEntity<List<DataStandardDTO>> publishOrOff(@PathVariable(name = "organizationId") Long tenantId,
                                                              @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                              @RequestBody List<DataStandardDTO> dataStandardDTOList) {
        dataStandardDTOList.forEach(dataStandardDTO -> {
            dataStandardDTO.setTenantId(tenantId);
            dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
            standardHandlers.forEach(standardHandler -> standardHandler.valid(dataStandardDTO));
        });
        dataStandardDTOList.forEach(dataStandardService::publishOrOff);
        return Results.success(dataStandardDTOList);
    }


    @ApiOperation(value = "根据唯一索引查询数据标准")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/get-by-unique")
    public ResponseEntity<DataStandardDTO> getByUnique(@PathVariable(name = "organizationId") Long tenantId,
                                                       @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                       DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        List<DataStandardDTO> standardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardName())
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId())
                        .andEqualTo(DataStandard.FIELD_PROJECT_ID, dataStandardDTO.getProjectId()))
                .build());
        dataStandardDTO = null;
        if (CollectionUtils.isNotEmpty(standardDTOList)) {
            dataStandardDTO = standardDTOList.get(0);
        }
        return Results.success(dataStandardDTO);
    }

    @ApiOperation(value = "批量删除")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/batch-delete")
    public ResponseEntity<List<DataStandardDTO>> batchDelete(@PathVariable(name = "organizationId") Long tenantId,
                                                             @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                             @RequestBody List<DataStandardDTO> dataStandardDTOList) {
        if (CollectionUtils.isNotEmpty(dataStandardDTOList)) {
            dataStandardDTOList.forEach(dataStandardDTO -> {
                dataStandardDTO.setTenantId(tenantId);
                dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
                dataStandardService.delete(dataStandardDTO);
            });
        }
        return Results.success(dataStandardDTOList);
    }

    @ApiOperation(value = "数据标准落标")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/standard-aim")
    public ResponseEntity<Void> standardAim(@PathVariable(name = "organizationId") Long tenantId,
                                            @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                            @RequestBody List<StandardAimDTO> standardAimDTOList) {
        dataStandardService.aim(tenantId, standardAimDTOList, projectId);
        return Results.success();
    }

    @ApiOperation(value = "数据标准批量关联评估方案")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch-relate-plan")
    public ResponseEntity<Void> batchRelatePlan(@PathVariable(name = "organizationId") Long tenantId,
                                                @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                @RequestBody List<StandardAimDTO> standardAimDTOList) {
        dataStandardService.batchRelatePlan(tenantId, standardAimDTOList, projectId);
        return Results.success();
    }


    @ApiOperation(value = "导出数据标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = DataStandardExportDTO.class)
    public ResponseEntity<List<DataStandardExportDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                        DataStandardDTO dto,
                                                        ExportParam exportParam,
                                                        HttpServletResponse response) {

        dto.setTenantId(tenantId);
        dto.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        List<DataStandardExportDTO> dtoList =
                dataStandardService.export(dto, exportParam);
        return Results.success(dtoList);
    }


    @ApiOperation(value = "数据标准转换标准规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/standard-to-rule")
    public ResponseEntity<BatchPlanFieldDTO> standardToRule(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                            Long standardId) {
        BatchPlanFieldDTO batchPlanFieldDTO = dataStandardService.standardToRule(standardId);
        return Results.success(batchPlanFieldDTO);
    }


    @ApiOperation(value = "字段元数据关联数据标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/field-aim-standard")
    public ResponseEntity<Void> fieldAimStandard(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                 @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                 @RequestBody AssetFieldDTO assetFieldDTO) {
        assetFieldDTO.setTenantId(tenantId);
        dataStandardService.fieldAimStandard(assetFieldDTO, projectId);
        return Results.success();
    }

    @ApiOperation(value = "根据字段查询数据标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/standard-by-field")
    public ResponseEntity<List<DataStandardDTO>> standardByField(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                                 @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                                 @RequestBody AssetFieldDTO assetFieldDTO) {
        assetFieldDTO.setTenantId(tenantId);
        List<DataStandardDTO> dataStandardDTOList = dataStandardService.standardByField(assetFieldDTO, projectId);
        return Results.success(dataStandardDTOList);
    }

    @ApiOperation(value = "元数据数据标准详情")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/asset-detail/{standardId}")
    public ResponseEntity<DataStandardDTO> assetDetail(@PathVariable(name = "organizationId") Long tenantId,
                                                       @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                       @PathVariable(name = "standardId") Long standardId) {
        return Results.success(dataStandardService.assetDetail(tenantId, standardId,projectId));
    }

    @ApiOperation(value = "修改数据标准属性")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/update-standard-property")
    public ResponseEntity<DataStandardDTO> updateStandardProperty(@PathVariable(name = "organizationId") Long tenantId,
                                                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                                  @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dataStandardRepository.updateDTOAllColumnWhereTenant(dataStandardDTO, tenantId);
        return Results.success(dataStandardDTO);
    }


    @ApiOperation(value = "根据数据标准编码查询责任人，用于审批规则")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/find-charger/{dataStandardCode}")
    public ResponseEntity<List<AssigneeUserDTO>> findCharger(@PathVariable(name = "organizationId") Long tenantId,
                                                             @PathVariable String dataStandardCode) {
        return Results.success(dataStandardService.findCharger(tenantId, dataStandardCode));
    }

    @ApiOperation(value = "工作流发布接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/publish-by-workflow")
    public ResponseEntity<DataStandardDTO> publishByWorkflow(@PathVariable(name = "organizationId") Long tenantId,
                                                             @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardService.startWorkFlow(WorkFlowConstant.DataStandard.ONLINE_WORKFLOW_KEY, dataStandardDTO, "ONLINE");
        return Results.success(dataStandardDTO);
    }


    @ApiOperation(value = "数据标准上线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflow-callback/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowCallback(@PathVariable(name = "organizationId") Long tenantId,
                                                                  @PathVariable String dataStandardCode,
                                                                  @RequestParam String nodeApproveResult) {
        dataStandardService.onlineWorkflowCallback(dataStandardCode,nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "数据标准下线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflow-callback/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowCallback(@PathVariable(name = "organizationId") Long tenantId,
                                                                  @PathVariable String dataStandardCode,
                                                                  @RequestParam String nodeApproveResult) {
        dataStandardService.offlineWorkflowCallback(dataStandardCode,nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "流程信息表单-申请信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data-apply-info")
    public ResponseEntity<StandardApprovalDTO> dataApplyInfo(@PathVariable("organizationId") Long tenantId,
                                                              Long approvalId) {
        return Results.success(dataStandardService.dataApplyInfo(tenantId, approvalId));
    }

    @ApiOperation(value = "数据标准信息-审批表单用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data-info")
    public ResponseEntity<DataStandardDTO> dataInfo(@PathVariable(name = "organizationId") Long tenantId,
                                                  Long approvalId) {
        return Results.success(dataStandardService.dataInfo(tenantId, approvalId));
    }

    @ApiOperation(value = "数据标准上线工作流通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflow-success/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowSuccess(@PathVariable(name = "organizationId") Long tenantId, @PathVariable String dataStandardCode) {
        //兼容旧版工作流
        dataStandardService.onlineWorkflowCallback(dataStandardCode, WorkflowConstant.ApproveAction.APPROVED);
        return Results.success();
    }

    @ApiOperation(value = "数据标准上线工作流拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflow-fail/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowFail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable String dataStandardCode) {
        //兼容旧版工作流
        dataStandardService.onlineWorkflowCallback(dataStandardCode,WorkflowConstant.ApproveAction.REJECTED);
        return Results.success();
    }

    @ApiOperation(value = "数据标准下线工作流发布通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflow-success/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowSuccess(@PathVariable(name = "organizationId") Long tenantId, @PathVariable String dataStandardCode) {
        //兼容旧版工作流
        dataStandardService.offlineWorkflowCallback(dataStandardCode,WorkflowConstant.ApproveAction.APPROVED);
        return Results.success();
    }

    @ApiOperation(value = "数据标准下线工作流发布拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflow-fail/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowFail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable String dataStandardCode) {
        //兼容旧版工作流
        dataStandardService.offlineWorkflowCallback(dataStandardCode,WorkflowConstant.ApproveAction.REJECTED);
        return Results.success();
    }


    @ApiOperation(value = "数据标准上线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflowing/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowing(@PathVariable(name = "organizationId") Long tenantId, @PathVariable String dataStandardCode) {
        //兼容旧版工作流
        dataStandardService.workflowing(tenantId, dataStandardCode,ONLINE_APPROVING);
        return Results.success();
    }

    @ApiOperation(value = "数据标准下线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflowing/{dataStandardCode}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowing(@PathVariable(name = "organizationId") Long tenantId, @PathVariable String dataStandardCode) {
        //兼容旧版工作流
        dataStandardService.workflowing(tenantId, dataStandardCode, OFFLINE_APPROVING);
        return Results.success();
    }
}
