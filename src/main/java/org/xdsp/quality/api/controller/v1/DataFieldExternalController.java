package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.core.base.BaseController;
import org.hzero.core.base.Result;
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
import org.xdsp.quality.infra.export.dto.FieldStandardExportDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.xdsp.quality.infra.constant.StandardConstant.Status.OFFLINE_APPROVING;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.ONLINE_APPROVING;

/**
 * <p>字段标准表 管理 API</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@RestController("dataFieldExternalController.v1")
@RequestMapping("/v1/data-fields/external")
public class DataFieldExternalController extends BaseController {

    private final DataFieldService dataFieldService;

    private final DataFieldRepository dataFieldRepository;

    public DataFieldExternalController(DataFieldService dataFieldService, DataFieldRepository dataFieldRepository) {
        this.dataFieldService = dataFieldService;
        this.dataFieldRepository = dataFieldRepository;
    }


    @ApiOperation(value = "字段标准上线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflow-callback")
    public ResponseEntity<?> onlineWorkflowCallback(@RequestParam(name = "organizationId") Long tenantId,
                                                                  @RequestParam Long fieldId,
                                                                  @RequestParam String nodeApproveResult) {
        Result<?> result = new Result<>();
        try {
            dataFieldService.onlineWorkflowCallback(fieldId,nodeApproveResult);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "字段标准下线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflow-callback")
    public ResponseEntity<?> offlineWorkflowCallback(@RequestParam(name = "organizationId") Long tenantId,
                                                                  @RequestParam Long fieldId,
                                                                  @RequestParam String nodeApproveResult) {
        Result<?> result = new Result<>();
        try {
            dataFieldService.offlineWorkflowCallback(fieldId,nodeApproveResult);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "字段标准上线工作流通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflow-success")
    public ResponseEntity<?> onlineWorkflowSuccess(@RequestParam(name = "organizationId") Long tenantId,
                                                                 @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                                 @RequestParam Long fieldId) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataFieldService.onlineWorkflowCallback(fieldId, WorkflowConstant.ApproveAction.APPROVED);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "字段标准上线工作流拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflow-fail")
    public ResponseEntity<?> onlineWorkflowFail(@RequestParam(name = "organizationId") Long tenantId, @RequestParam Long fieldId) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataFieldService.onlineWorkflowCallback(fieldId,WorkflowConstant.ApproveAction.REJECTED);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "字段标准下线工作流发布通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflow-success")
    public ResponseEntity<?> offlineWorkflowSuccess(@RequestParam(name = "organizationId") Long tenantId, @RequestParam Long fieldId) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataFieldService.offlineWorkflowCallback(fieldId,WorkflowConstant.ApproveAction.APPROVED);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "字段标准下线工作流发布拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflow-fail")
    public ResponseEntity<?> offlineWorkflowFail(@RequestParam(name = "organizationId") Long tenantId, @RequestParam Long fieldId) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataFieldService.offlineWorkflowCallback(fieldId,WorkflowConstant.ApproveAction.REJECTED);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "字段标准上线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflowing")
    public ResponseEntity<?> onlineWorkflowing(@RequestParam(name = "organizationId") Long tenantId, @RequestParam Long fieldId) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataFieldService.workflowing(tenantId, fieldId, ONLINE_APPROVING);

            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "字段标准下线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflowing")
    public ResponseEntity<?> offlineWorkflowing(@RequestParam(name = "organizationId") Long tenantId, @RequestParam Long fieldId) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataFieldService.workflowing(tenantId, fieldId, OFFLINE_APPROVING);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

}
