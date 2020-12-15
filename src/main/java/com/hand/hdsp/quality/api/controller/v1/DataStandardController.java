package com.hand.hdsp.quality.api.controller.v1;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.quality.api.dto.AssetFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public DataStandardController(DataStandardService dataStandardService, DataStandardRepository dataStandardRepository) {
        this.dataStandardService = dataStandardService;
        this.dataStandardRepository = dataStandardRepository;
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
    public ResponseEntity<Page<DataStandardDTO>> list(@PathVariable(name = "organizationId") Long tenantId, DataStandardDTO dataStandardDTO, PageRequest pageRequest) {
        dataStandardDTO.setTenantId(tenantId);
        return Results.success(dataStandardService.list(pageRequest, dataStandardDTO));
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
    public ResponseEntity<DataStandardDTO> detail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable(name = "standardId") Long standardId) {
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
    public ResponseEntity<DataStandardDTO> create(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
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
    public ResponseEntity<DataStandardDTO> delete(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
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
    public ResponseEntity<DataStandardDTO> update(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
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
    public ResponseEntity<DataStandardDTO> updateStatus(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
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
    public ResponseEntity<List<DataStandardDTO>> publishOrOff(@PathVariable(name = "organizationId") Long tenantId, @RequestBody List<DataStandardDTO> dataStandardDTOList) {
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
    public ResponseEntity<DataStandardDTO> getByUnique(@PathVariable(name = "organizationId") Long tenantId, DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        List<DataStandardDTO> standardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardName())
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        dataStandardDTO=null;
        if (CollectionUtils.isNotEmpty(standardDTOList)){
            dataStandardDTO=standardDTOList.get(0);
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
    public ResponseEntity<List<DataStandardDTO>> batchDelete(@PathVariable(name = "organizationId") Long tenantId, @RequestBody List<DataStandardDTO> dataStandardDTOList) {
        if(CollectionUtils.isNotEmpty(dataStandardDTOList)){
            dataStandardDTOList.forEach(dataStandardService::delete);
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
    public ResponseEntity<Void> standardAim(@PathVariable(name = "organizationId") Long tenantId, @RequestBody List<StandardAimDTO> standardAimDTOList) {
        dataStandardService.aim(tenantId,standardAimDTOList);
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
    public ResponseEntity<Void> batchRelatePlan(@PathVariable(name = "organizationId") Long tenantId, @RequestBody List<StandardAimDTO> standardAimDTOList) {
        dataStandardService.batchRelatePlan(standardAimDTOList);
        return Results.success();
    }


    @ApiOperation(value = "导出数据标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = DataStandardDTO.class)
    public ResponseEntity<List<DataStandardDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                            DataStandardDTO dto,
                                                            ExportParam exportParam,
                                                            HttpServletResponse response,
                                                            PageRequest pageRequest) {

        dto.setTenantId(tenantId);
        List<DataStandardDTO> dtoList =
                dataStandardService.export(dto, exportParam, pageRequest);
        return Results.success(dtoList);
    }


    @ApiOperation(value = "数据标准转换标准规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/standard-to-rule")
    public ResponseEntity<BatchPlanFieldDTO> standardToRule(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                            Long standardId) {
        BatchPlanFieldDTO batchPlanFieldDTO =dataStandardService.standardToRule(standardId);
        return Results.success(batchPlanFieldDTO);
    }


    @ApiOperation(value = "字段元数据关联数据标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/field-aim-standard")
    public ResponseEntity<Void> fieldAimStandard(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                           @RequestBody AssetFieldDTO assetFieldDTO) {
        assetFieldDTO.setTenantId(tenantId);
        dataStandardService.fieldAimStandard(assetFieldDTO);
        return Results.success();
    }

    @ApiOperation(value = "根据字段查询数据标准")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/standard-by-field")
    public ResponseEntity<List<DataStandardDTO>> standardByField(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                 @RequestBody AssetFieldDTO assetFieldDTO) {
        assetFieldDTO.setTenantId(tenantId);
        List<DataStandardDTO> dataStandardDTOList=dataStandardService.standardByField(assetFieldDTO);
        return Results.success(dataStandardDTOList);
    }
}
