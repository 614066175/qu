package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.WorkOrderDTO;
import org.xdsp.quality.app.service.WorkOrderService;
import org.xdsp.quality.domain.entity.WorkOrder;
import org.xdsp.quality.domain.repository.WorkOrderRepository;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p> 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@RestController("workOrderController.v1")
@RequestMapping("/v1/{organizationId}/work-orders")
public class WorkOrderController extends BaseController {

    private WorkOrderRepository workOrderRepository;
    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderRepository workOrderRepository, WorkOrderService workOrderService) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderService = workOrderService;
    }

    @ApiOperation(value = "列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  WorkOrderDTO workOrderDTO, @ApiIgnore @SortDefault(value = WorkOrder.FIELD_WORK_ORDER_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        workOrderDTO.setTenantId(tenantId);
        Page<WorkOrderDTO> list = workOrderRepository.pageAndSortDTO(pageRequest, workOrderDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "workOrderId",
            value = "主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{workOrderId}")
    public ResponseEntity<?> detail(@PathVariable Long workOrderId) {
        WorkOrderDTO workOrderDTO = workOrderService.detail(workOrderId);
        return Results.success(workOrderDTO);
    }

    @ApiOperation(value = "质量工单-发起整改")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> launchUpdate(@PathVariable("organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          @RequestBody List<WorkOrderDTO> workOrderDTOList) {
        workOrderDTOList.forEach(workOrderDTO -> {
            workOrderDTO.setTenantId(tenantId);
            workOrderDTO.setProjectId(projectId);
        });
        return Results.success(workOrderService.launchUpdate(workOrderDTOList));
    }


    @ApiOperation(value = "质量工单-撤回整改")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/revoke-order")
    public ResponseEntity<?> revokeOrder(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestBody List<Long> workOrderIdList) {
        return Results.success(workOrderService.revokeOrder(workOrderIdList));
    }

    @ApiOperation(value = "修改")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody WorkOrderDTO workOrderDTO) {
        workOrderRepository.updateDTOWhereTenant(workOrderDTO, tenantId);
        return Results.success(workOrderDTO);
    }

    @ApiOperation(value = "删除")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody WorkOrderDTO workOrderDTO) {
        workOrderDTO.setTenantId(tenantId);
        workOrderRepository.deleteByPrimaryKey(workOrderDTO);
        return Results.success();
    }


    @ApiOperation(value = "我处理的-质量工单列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/order-todo")
    public ResponseEntity<?> orderTodo(@PathVariable(name = "organizationId") Long tenantId,
                                       @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                       WorkOrderDTO workOrderDTO, @ApiIgnore @SortDefault(value = WorkOrder.FIELD_WORK_ORDER_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        workOrderDTO.setTenantId(tenantId);
        workOrderDTO.setProjectId(projectId);
        return Results.success(workOrderService.orderTodo(workOrderDTO, pageRequest));
    }

    @ApiOperation(value = "我发起的-质量工单列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/order-apply")
    public ResponseEntity<?> orderApply(@PathVariable(name = "organizationId") Long tenantId,
                                        @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                        WorkOrderDTO workOrderDTO, @ApiIgnore @SortDefault(value = WorkOrder.FIELD_WORK_ORDER_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        workOrderDTO.setTenantId(tenantId);
        workOrderDTO.setProjectId(projectId);
        return Results.success(workOrderService.orderApply(workOrderDTO, pageRequest));
    }

    @ApiOperation(value = "质量工单-批量接收")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch-receive")
    public ResponseEntity<?> batchReceive(@PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          @RequestBody List<WorkOrderDTO> workOrderDTOList) {
        workOrderDTOList.forEach(workOrderDTO -> {
            workOrderDTO.setTenantId(tenantId);
            workOrderDTO.setProjectId(projectId);
        });
        return Results.success(workOrderService.batchReceive(workOrderDTOList));
    }

    @ApiOperation(value = "质量工单-批量拒绝")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch-refuse")
    public ResponseEntity<?> batchRefuse(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         @RequestBody List<WorkOrderDTO> workOrderDTOList) {
        workOrderDTOList.forEach(workOrderDTO -> {
            workOrderDTO.setTenantId(tenantId);
            workOrderDTO.setProjectId(projectId);
        });
        return Results.success(workOrderService.batchRefuse(workOrderDTOList));
    }

    @ApiOperation(value = "质量工单-操作流程信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/order-operate-info/{workOrderId}")
    public ResponseEntity<?> oderOperateInfo(@PathVariable(name = "organizationId") Long tenantId,
                                             @PathVariable Long workOrderId) {
        return Results.success(workOrderService.oderOperateInfo(workOrderId));
    }

    @ApiOperation(value = "质量工单-开始处理")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/start-process")
    public ResponseEntity<?> startProcess(@PathVariable(name = "organizationId") Long tenantId,
                                          @RequestBody Long workOrderId) {
        return Results.success(workOrderService.startProcess(workOrderId));
    }

    @ApiOperation(value = "质量工单-转交")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/order-assign")
    public ResponseEntity<?> orderAssign(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestBody WorkOrderDTO workOrderDTO) {
        workOrderDTO.setTenantId(tenantId);
        return Results.success(workOrderService.orderAssign(workOrderDTO));
    }

    @ApiOperation(value = "质量工单-提交")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/order-submit")
    public ResponseEntity<?> orderSubmit(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestBody WorkOrderDTO workOrderDTO) {
        workOrderDTO.setTenantId(tenantId);
        return Results.success(workOrderService.orderSubmit(workOrderDTO));
    }

    @ApiOperation(value = "工单处理-提醒一下")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/order-remind/{workOrderId}")
    public ResponseEntity<?> orderRemind(@PathVariable(name = "organizationId") Long tenantId,
                                         @PathVariable Long workOrderId) {
        return Results.success(workOrderService.orderRemind(workOrderId));
    }

}
