package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.core.base.Result;
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
@RestController("dataStandardExternalController.v1")
@RequestMapping("/v1/data-standard/external")
public class DataStandardExternalController {

    private final DataStandardService dataStandardService;

    private final DataStandardRepository dataStandardRepository;

    private final StandardDocMapper standardDocMapper;

    private final List<StandardHandler> standardHandlers;

    public DataStandardExternalController(DataStandardService dataStandardService, DataStandardRepository dataStandardRepository, StandardDocMapper standardDocMapper, List<StandardHandler> standardHandlers) {
        this.dataStandardService = dataStandardService;
        this.dataStandardRepository = dataStandardRepository;
        this.standardDocMapper = standardDocMapper;
        this.standardHandlers = standardHandlers;
    }


    @ApiOperation(value = "数据标准上线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflow-callback")
    public ResponseEntity<?> onlineWorkflowCallback(@RequestParam(name = "organizationId") Long tenantId,
                                                                  @RequestParam String dataStandardCode,
                                                                  @RequestParam String nodeApproveResult) {
        Result<?> result = new Result<>();
        try {
            dataStandardService.onlineWorkflowCallback(dataStandardCode, nodeApproveResult);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "数据标准下线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflow-callback")
    public ResponseEntity<?> offlineWorkflowCallback(@RequestParam(name = "organizationId") Long tenantId,
                                                                   @RequestParam String dataStandardCode,
                                                                   @RequestParam String nodeApproveResult) {
        Result<?> result = new Result<>();
        try {
            dataStandardService.offlineWorkflowCallback(dataStandardCode, nodeApproveResult);
            result.setSuccess(true);
        }catch (Exception e){
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "数据标准上线工作流通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflow-success")
    public ResponseEntity<?> onlineWorkflowSuccess(@RequestParam(name = "organizationId") Long tenantId, @RequestParam String dataStandardCode) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try{
            dataStandardService.onlineWorkflowCallback(dataStandardCode, WorkflowConstant.ApproveAction.APPROVED);
            result.setSuccess(true);
        }catch (Exception e){
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "数据标准上线工作流拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflow-fail")
    public ResponseEntity<?> onlineWorkflowFail(@RequestParam(name = "organizationId") Long tenantId, @RequestParam String dataStandardCode) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataStandardService.onlineWorkflowCallback(dataStandardCode, WorkflowConstant.ApproveAction.REJECTED);
            result.setSuccess(true);
        }catch (Exception e){
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "数据标准下线工作流发布通过事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflow-success")
    public ResponseEntity<?> offlineWorkflowSuccess(@RequestParam(name = "organizationId") Long tenantId, @RequestParam String dataStandardCode) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataStandardService.offlineWorkflowCallback(dataStandardCode, WorkflowConstant.ApproveAction.APPROVED);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "数据标准下线工作流发布拒绝事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflow-fail")
    public ResponseEntity<?> offlineWorkflowFail(@RequestParam(name = "organizationId") Long tenantId, @RequestParam String dataStandardCode) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataStandardService.offlineWorkflowCallback(dataStandardCode, WorkflowConstant.ApproveAction.REJECTED);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "数据标准上线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/online-workflowing")
    public ResponseEntity<?> onlineWorkflowing(@RequestParam(name = "organizationId") Long tenantId, @RequestParam String dataStandardCode) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataStandardService.workflowing(tenantId, dataStandardCode, ONLINE_APPROVING);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "数据标准下线审批中事件")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-workflowing")
    public ResponseEntity<?> offlineWorkflowing(@RequestParam(name = "organizationId") Long tenantId, @RequestParam String dataStandardCode) {
        //兼容旧版工作流
        Result<?> result = new Result<>();
        try {
            dataStandardService.workflowing(tenantId, dataStandardCode, OFFLINE_APPROVING);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }
}
