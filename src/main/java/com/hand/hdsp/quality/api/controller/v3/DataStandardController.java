package com.hand.hdsp.quality.api.controller.v3;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
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
@RestController("dataStandardController.v3")
@RequestMapping("/v3/{organizationId}/data-standard")
public class DataStandardController {

    private final DataStandardService dataStandardService;

    private final DataStandardRepository dataStandardRepository;

    private final StandardExtraRepository standardExtraRepository;

    public DataStandardController(DataStandardService dataStandardService, DataStandardRepository dataStandardRepository, StandardExtraRepository standardExtraRepository) {
        this.dataStandardService = dataStandardService;
        this.dataStandardRepository = dataStandardRepository;
        this.standardExtraRepository = standardExtraRepository;
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
    public ResponseEntity<?> detail(@PathVariable(name = "organizationId") Long tenantId, @PathVariable(name = "standardId") Long standardId) {
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
    public ResponseEntity<?> create(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
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
    public ResponseEntity<?> delete(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
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
    public ResponseEntity<?> update(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardRepository.updateByDTOPrimaryKey(dataStandardDTO);
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
    public ResponseEntity<?> updateStatus(@PathVariable(name = "organizationId") Long tenantId, @RequestBody DataStandardDTO dataStandardDTO) {
        dataStandardDTO.setTenantId(tenantId);
        dataStandardService.updateStatus(dataStandardDTO);
        return Results.success(dataStandardDTO);
    }
}
