package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.app.service.DataStandardVersionService;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;
import com.hand.hdsp.quality.domain.repository.DataStandardVersionRepository;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>数据标准版本表 管理 API</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-26 16:00:31
 */
@RestController("dataStandardVersionController.v1")
@RequestMapping("/v1/{organizationId}/data-standard-versions")
public class DataStandardVersionController extends BaseController {

    private DataStandardVersionRepository dataStandardVersionRepository;

    private DataStandardVersionService dataStandardVersionService;

    public DataStandardVersionController(DataStandardVersionRepository dataStandardVersionRepository, DataStandardVersionService dataStandardVersionService) {
        this.dataStandardVersionRepository = dataStandardVersionRepository;
        this.dataStandardVersionService = dataStandardVersionService;
    }

    @ApiOperation(value = "数据标准版本表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  DataStandardVersionDTO dataStandardVersionDTO, @ApiIgnore @SortDefault(value = DataStandardVersion.FIELD_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dataStandardVersionDTO.setTenantId(tenantId);
        Page<DataStandardVersionDTO> list=dataStandardVersionService.list(pageRequest,dataStandardVersionDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "数据标准版本表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "versionId",
            value = "数据标准版本表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{versionId}")
    public ResponseEntity<?> detail(@PathVariable Long versionId) {
        return Results.success(dataStandardVersionService.detail(versionId));
    }

    @ApiOperation(value = "创建数据标准版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody DataStandardVersionDTO dataStandardVersionDTO) {
        dataStandardVersionDTO.setTenantId(tenantId);
        this.validObject(dataStandardVersionDTO);
        dataStandardVersionRepository.insertDTOSelective(dataStandardVersionDTO);
        return Results.success(dataStandardVersionDTO);
    }

    @ApiOperation(value = "修改数据标准版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody DataStandardVersionDTO dataStandardVersionDTO) {
                dataStandardVersionRepository.updateDTOWhereTenant(dataStandardVersionDTO, tenantId);
        return Results.success(dataStandardVersionDTO);
    }

    @ApiOperation(value = "删除数据标准版本表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody DataStandardVersionDTO dataStandardVersionDTO) {
                dataStandardVersionDTO.setTenantId(tenantId);
        dataStandardVersionRepository.deleteByPrimaryKey(dataStandardVersionDTO);
        return Results.success();
    }
}
