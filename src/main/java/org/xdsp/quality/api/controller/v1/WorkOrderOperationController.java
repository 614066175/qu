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
import org.xdsp.quality.api.dto.WorkOrderOperationDTO;
import org.xdsp.quality.domain.entity.WorkOrderOperation;
import org.xdsp.quality.domain.repository.WorkOrderOperationRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p> 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@RestController("workOrderOperationController.v1")
@RequestMapping("/v1/{organizationId}/work-order-operations")
public class WorkOrderOperationController extends BaseController {

    private WorkOrderOperationRepository workOrderOperationRepository;

    public WorkOrderOperationController(WorkOrderOperationRepository workOrderOperationRepository) {
        this.workOrderOperationRepository = workOrderOperationRepository;
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
                WorkOrderOperationDTO workOrderOperationDTO, @ApiIgnore @SortDefault(value = WorkOrderOperation.FIELD_ORDER_OPERATE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        workOrderOperationDTO.setTenantId(tenantId);
        Page<WorkOrderOperationDTO> list = workOrderOperationRepository.pageAndSortDTO(pageRequest, workOrderOperationDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "operateId",
            value = "主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{operateId}")
    public ResponseEntity<?> detail(@PathVariable Long operateId) {
        WorkOrderOperationDTO workOrderOperationDTO = workOrderOperationRepository.selectDTOByPrimaryKeyAndTenant(operateId);
        return Results.success(workOrderOperationDTO);
    }

    @ApiOperation(value = "创建")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody WorkOrderOperationDTO workOrderOperationDTO) {
        workOrderOperationDTO.setTenantId(tenantId);
        this.validObject(workOrderOperationDTO);
        workOrderOperationRepository.insertDTOSelective(workOrderOperationDTO);
        return Results.success(workOrderOperationDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody WorkOrderOperationDTO workOrderOperationDTO) {
                workOrderOperationRepository.updateDTOWhereTenant(workOrderOperationDTO, tenantId);
        return Results.success(workOrderOperationDTO);
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
                                    @RequestBody WorkOrderOperationDTO workOrderOperationDTO) {
                workOrderOperationDTO.setTenantId(tenantId);
        workOrderOperationRepository.deleteByPrimaryKey(workOrderOperationDTO);
        return Results.success();
    }
}
