package com.hand.hdsp.quality.api.controller.v1;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.quality.api.dto.AssigneeUserDTO;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import com.hand.hdsp.quality.infra.mapper.StandardDocMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<DataFieldDTO>> list(@PathVariable(name = "organizationId") Long tenantId, DataFieldDTO dataFieldDTO, PageRequest pageRequest) {
        dataFieldDTO.setTenantId(tenantId);
        Page<DataFieldDTO> list = dataFieldService.list(pageRequest, dataFieldDTO);
        list.getContent().stream()
                .peek(dto -> {
                    dto.setCreatedByName(standardDocMapper.selectUserNameById(dto.getCreatedBy()));
                    dto.setLastUpdatedByName(standardDocMapper.selectUserNameById(dto.getLastUpdatedBy()));
                })
                .collect(Collectors.toList());
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
    public ResponseEntity<DataFieldDTO> create(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
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
                                         @RequestBody List<DataFieldDTO> dataFieldDTOList) {
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            dataFieldDTOList.forEach(dataFieldService::delete);
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
    public ResponseEntity<DataFieldDTO> update(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
        dataFieldRepository.updateByDTOPrimaryKey(dataFieldDTO);
        return Results.success(dataFieldDTO);
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
    public ResponseEntity<DataFieldDTO> updateStatus(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
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
    public ResponseEntity<Void> standardAim(@PathVariable(name = "organizationId") Long tenantId, @RequestBody List<StandardAimDTO> standardAimDTOList) {
        dataFieldService.aim(tenantId,standardAimDTOList);
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
    public ResponseEntity<List<DataFieldDTO>> publishOrOff(@PathVariable(name = "organizationId") Long tenantId, @RequestBody List<DataFieldDTO> dataFieldDTOList) {
        dataFieldDTOList.forEach(dataFieldService::publishOrOff);
        return Results.success(dataFieldDTOList);
    }

    @ApiOperation(value = "导出字段标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = DataFieldDTO.class)
    public ResponseEntity<List<DataFieldDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                     DataFieldDTO dto,
                                                     ExportParam exportParam,
                                                     HttpServletResponse response,
                                                     PageRequest pageRequest) {

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        dto.setTenantId(tenantId);
        List<DataFieldDTO> dtoList =
                dataFieldService.export(dto, exportParam, pageRequest);
        return Results.success(dtoList);
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
    public ResponseEntity<DataStandardDTO> onlineWorkflowSuccess(@PathVariable(name = "organizationId") Long tenantId, @PathVariable Long fieldId) {
        dataFieldService.onlineWorkflowSuccess(tenantId,fieldId);
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
        dataFieldService.onlineWorkflowFail(tenantId,fieldId);
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
        dataFieldService.offlineWorkflowSuccess(tenantId,fieldId);
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
        dataFieldService.offlineWorkflowFail(tenantId,fieldId);
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
        dataFieldService.onlineWorkflowing(tenantId,fieldId);
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
        dataFieldService.offlineWorkflowing(tenantId,fieldId);
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

}
