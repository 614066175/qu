package com.hand.hdsp.quality.api.controller.v1;

import java.util.List;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
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

    public DataFieldController(DataFieldService dataFieldService, DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository) {
        this.dataFieldService = dataFieldService;
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
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
        return Results.success(dataFieldService.list(pageRequest, dataFieldDTO));
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
    public ResponseEntity<?> create(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataFieldDTO dataFieldDTO) {
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
    public ResponseEntity<?> detail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable(name = "fieldId") Long fieldId) {
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
    public ResponseEntity<?> batchDelete(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody List<DataFieldDTO> dataFieldDTOList) {
        if(CollectionUtils.isNotEmpty(dataFieldDTOList)){
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
    public ResponseEntity<?> update(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataFieldDTO dataFieldDTO) {
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
        dataFieldService.aim(standardAimDTOList);
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
    public ResponseEntity<DataFieldDTO> publishOrOff(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataFieldDTO dataFieldDTO) {
        dataFieldDTO.setTenantId(tenantId);
        dataFieldService.publishOrOff(dataFieldDTO);
        return Results.success(dataFieldDTO);
    }

}
