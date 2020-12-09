package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.app.service.DataFieldVersionService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;
import com.hand.hdsp.quality.domain.repository.DataFieldVersionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>字段标准版本表 管理 API</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@RestController("dataFieldVersionController.v1")
@RequestMapping("/v1/{organizationId}/data-field-versions")
public class DataFieldVersionController extends BaseController {

    private DataFieldVersionRepository dataFieldVersionRepository;

    private DataFieldVersionService dataFieldVersionService;

    public DataFieldVersionController(DataFieldVersionRepository dataFieldVersionRepository, DataFieldVersionService dataFieldVersionService) {
        this.dataFieldVersionRepository = dataFieldVersionRepository;
        this.dataFieldVersionService = dataFieldVersionService;
    }

    @ApiOperation(value = "字段标准版本表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<DataFieldVersionDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                DataFieldVersionDTO dataFieldVersionDTO, @ApiIgnore @SortDefault(value = DataFieldVersion.FIELD_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dataFieldVersionDTO.setTenantId(tenantId);
        Page<DataFieldVersionDTO> list = dataFieldVersionService.list(pageRequest, dataFieldVersionDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "字段标准版本表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "versionId",
            value = "字段标准版本表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{versionId}")
    public ResponseEntity<DataFieldVersionDTO> detail(@PathVariable Long versionId) {
        DataFieldVersionDTO dataFieldVersionDTO = dataFieldVersionService.detail(versionId);
        return Results.success(dataFieldVersionDTO);
    }

    @ApiOperation(value = "创建字段标准版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DataFieldVersionDTO> create(@PathVariable("organizationId") Long tenantId, @RequestBody DataFieldVersionDTO dataFieldVersionDTO) {
        dataFieldVersionDTO.setTenantId(tenantId);
        this.validObject(dataFieldVersionDTO);
        dataFieldVersionRepository.insertDTOSelective(dataFieldVersionDTO);
        return Results.success(dataFieldVersionDTO);
    }

    @ApiOperation(value = "修改字段标准版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DataFieldVersionDTO> update(@PathVariable("organizationId") Long tenantId, @RequestBody DataFieldVersionDTO dataFieldVersionDTO) {
                dataFieldVersionRepository.updateDTOWhereTenant(dataFieldVersionDTO, tenantId);
        return Results.success(dataFieldVersionDTO);
    }

    @ApiOperation(value = "删除字段标准版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<DataFieldVersionDTO> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody DataFieldVersionDTO dataFieldVersionDTO) {
                dataFieldVersionDTO.setTenantId(tenantId);
        dataFieldVersionRepository.deleteByPrimaryKey(dataFieldVersionDTO);
        return Results.success();
    }
}
