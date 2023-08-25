package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.app.service.DataFieldService;
import org.xdsp.quality.config.SwaggerTags;
import org.xdsp.quality.domain.repository.DataFieldRepository;
import org.xdsp.quality.domain.repository.StandardExtraRepository;
import org.xdsp.quality.infra.export.dto.FieldStandardExportDTO;
import org.xdsp.quality.infra.mapper.StandardDocMapper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.OFFLINE_APPROVING;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.ONLINE_APPROVING;

/**
 * <p>字段标准表 管理 API</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Api(tags = SwaggerTags.FIELD_STANDARD)
@RestController("dataFieldController.v1")
@RequestMapping("/v1/{organizationId}/data-fields")
public class DataFieldController extends BaseController {

    private final DataFieldService dataFieldService;

    private final DataFieldRepository dataFieldRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final StandardDocMapper standardDocMapper;

    public DataFieldController(DataFieldService dataFieldService, DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository, StandardDocMapper standardDocMapper) {
        this.dataFieldService = dataFieldService;
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.standardDocMapper = standardDocMapper;
    }

    @ApiOperation(value = "字段标准列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<Page<DataFieldDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                                                   @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                   DataFieldDTO dataFieldDTO, PageRequest pageRequest) {
        dataFieldDTO.setTenantId(tenantId);
        dataFieldDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        Page<DataFieldDTO> list = dataFieldService.list(pageRequest, dataFieldDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "字段标准创建")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DataFieldDTO> create(@PathVariable(name = "organizationId") Long tenantId,
                                               @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                               @RequestBody DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
        dataFieldDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dataFieldService.create(dataFieldDTO);
        return Results.success(dataFieldDTO);
    }


    @ApiOperation(value = "字段标准详情")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/{fieldId}")
    public ResponseEntity<DataFieldDTO> detail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable(name = "fieldId") Long fieldId) {
        return Results.success(dataFieldService.detail(tenantId, fieldId));
    }

    @ApiOperation(value = "字段标准批量删除")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> batchDelete(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                            @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                            @RequestBody List<DataFieldDTO> dataFieldDTOList) {
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            dataFieldDTOList.forEach(dataFieldDTO -> {
                dataFieldDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
                dataFieldDTO.setTenantId(tenantId);
                dataFieldService.delete(dataFieldDTO);
            });
        }
        return Results.success();
    }

    @ApiOperation(value = "字段标准修改")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DataFieldDTO> update(@PathVariable(name = "organizationId") Long tenantId,
                                               @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                               @RequestBody DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
        dataFieldDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        return Results.success(dataFieldService.update(dataFieldDTO));
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
    public ResponseEntity<DataFieldDTO> updateStatus(@PathVariable(name = "organizationId") Long tenantId,
                                                     @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                     @RequestBody DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
        dataFieldDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        dataFieldService.updateStatus(dataFieldDTO);
        return Results.success(dataFieldDTO);
    }

    @ApiOperation(value = "字段标准落标")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/field-standard-aim")
    public ResponseEntity<Void> standardAim(@PathVariable(name = "organizationId") Long tenantId,
                                            @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                            @RequestBody List<StandardAimDTO> standardAimDTOList) {
        dataFieldService.aim(tenantId, standardAimDTOList, projectId);
        return Results.success();
    }

    @ApiOperation(value = "发布字段标准")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/publish-off")
    public ResponseEntity<List<DataFieldDTO>> publishOrOff(@PathVariable(name = "organizationId") Long tenantId,
                                                           @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                           @RequestBody List<DataFieldDTO> dataFieldDTOList) {
        dataFieldDTOList.forEach(dataFieldDTO -> {
            dataFieldDTO.setTenantId(tenantId);
            dataFieldDTO.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
            dataFieldService.publishOrOff(dataFieldDTO);
        });
        return Results.success(dataFieldDTOList);
    }

    @ApiOperation(value = "分组导出字段标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = FieldStandardExportDTO.class)
    public ResponseEntity<List<FieldStandardExportDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                     @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                     DataFieldDTO dto,
                                                     ExportParam exportParam,
                                                     HttpServletResponse response) {

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        dto.setTenantId(tenantId);
        dto.setProjectId(XdspConstant.DEFAULT_PROJECT_ID);
        List<FieldStandardExportDTO> dtoList =
                dataFieldService.export(dto, exportParam);
        return Results.success(dtoList);
    }


    @ApiOperation(value = "字段标准上线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflow-callback/{fieldId}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowCallback(@PathVariable(name = "organizationId") Long tenantId,
                                                                  @PathVariable Long fieldId,
                                                                  @RequestParam String nodeApproveResult) {
        dataFieldService.onlineWorkflowCallback(fieldId,nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "字段标准下线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflow-callback/{fieldId}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowCallback(@PathVariable(name = "organizationId") Long tenantId,
                                                                  @PathVariable Long fieldId,
                                                                  @RequestParam String nodeApproveResult) {
        dataFieldService.offlineWorkflowCallback(fieldId,nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "根据字段标准ID查询责任人，用于审批规则")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/find-charger/{fieldId}")
    public ResponseEntity<List<AssigneeUserDTO>> findCharger(@PathVariable(name = "organizationId") Long tenantId, @PathVariable Long fieldId) {
        return Results.success(dataFieldService.findCharger(tenantId, fieldId));
    }


    @ApiOperation(value = "字段标准落标统计")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/field-aim-statistic")
    public ResponseEntity<DataFieldDTO> fieldAimStatistic(@PathVariable(name = "organizationId") Long tenantId,
                                                          @RequestBody  DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
        return Results.success(dataFieldService.fieldAimStatistic(dataFieldDTO));
    }


    @ApiOperation(value = "字段标准转换标准规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/standard-to-rule")
    public ResponseEntity<BatchPlanFieldDTO> standardToRule(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                            Long standardId, String columnType) {
        BatchPlanFieldDTO batchPlanFieldDTO = dataFieldService.standardToRule(standardId, columnType);
        return Results.success(batchPlanFieldDTO);
    }

    @ApiOperation(value = "字段元数据关联字段标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/field-aim-standard")
    public ResponseEntity<Void> fieldAimStandard(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                 @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                 @RequestBody AssetFieldDTO assetFieldDTO) {
        assetFieldDTO.setTenantId(tenantId);
        dataFieldService.fieldAimStandard(assetFieldDTO, projectId);
        return Results.success();
    }

    @ApiOperation(value = "根据字段查询数据标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/standard-by-field")
    public ResponseEntity<List<DataFieldDTO>> standardByField(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                                 @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                                 @RequestBody AssetFieldDTO assetFieldDTO) {
        assetFieldDTO.setTenantId(tenantId);
        List<DataFieldDTO> dataFieldDTOList = dataFieldService.standardByField(assetFieldDTO, projectId);
        return Results.success(dataFieldDTOList);
    }

    @ApiOperation(value = "流程信息表单-申请信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/field-apply-info")
    public ResponseEntity<StandardApprovalDTO> fieldApplyInfo(@PathVariable("organizationId") Long tenantId,
                                                           Long approvalId) {
        return Results.success(dataFieldService.fieldApplyInfo(tenantId, approvalId));
    }

    @ApiOperation(value = "字段标准信息-审批表单用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/field-info")
    public ResponseEntity<DataFieldDTO> fieldInfo(@PathVariable(name = "organizationId") Long tenantId,
                                              Long approvalId) {
        return Results.success(dataFieldService.fieldInfo(tenantId, approvalId));
    }

    @ApiOperation(value = "字段标准上线工作流通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflow-success/{fieldId}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowSuccess(@PathVariable(name = "organizationId") Long tenantId,
                                                                 @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                                 @PathVariable Long fieldId) {
        //兼容旧版工作流
        dataFieldService.onlineWorkflowCallback(fieldId, WorkflowConstant.ApproveAction.APPROVED);
        return Results.success();
    }

    @ApiOperation(value = "字段标准上线工作流拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflow-fail/{fieldId}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowFail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable Long fieldId) {
        //兼容旧版工作流
        dataFieldService.onlineWorkflowCallback(fieldId,WorkflowConstant.ApproveAction.REJECTED);
        return Results.success();
    }

    @ApiOperation(value = "字段标准下线工作流发布通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflow-success/{fieldId}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowSuccess(@PathVariable(name = "organizationId") Long tenantId, @PathVariable Long fieldId) {
        //兼容旧版工作流
        dataFieldService.offlineWorkflowCallback(fieldId,WorkflowConstant.ApproveAction.APPROVED);
        return Results.success();
    }

    @ApiOperation(value = "字段标准下线工作流发布拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflow-fail/{fieldId}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowFail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable Long fieldId) {
        //兼容旧版工作流
        dataFieldService.offlineWorkflowCallback(fieldId,WorkflowConstant.ApproveAction.REJECTED);
        return Results.success();
    }


    @ApiOperation(value = "字段标准上线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/online-workflowing/{fieldId}")
    public ResponseEntity<DataStandardDTO> onlineWorkflowing(@PathVariable(name = "organizationId") Long tenantId, @PathVariable Long fieldId) {
        //兼容旧版工作流
        dataFieldService.workflowing(tenantId, fieldId, ONLINE_APPROVING);
        return Results.success();
    }

    @ApiOperation(value = "字段标准下线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-workflowing/{fieldId}")
    public ResponseEntity<DataStandardDTO> offlineWorkflowing(@PathVariable(name = "organizationId") Long tenantId, @PathVariable Long fieldId) {
        //兼容旧版工作流
        dataFieldService.workflowing(tenantId, fieldId, OFFLINE_APPROVING);
        return Results.success();
    }
}
