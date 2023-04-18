package com.hand.hdsp.quality.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.app.service.ReferenceDataValueService;
import com.hand.hdsp.quality.domain.entity.ReferenceDataValue;
import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;
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
 * <p>参考数据值 管理 API</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@RestController("referenceDataValueController.v1")
@RequestMapping("/v1/{organizationId}/reference-data-values")
public class ReferenceDataValueController extends BaseController {

    private final ReferenceDataValueService referenceDataValueService;

    public ReferenceDataValueController(ReferenceDataValueService referenceDataValueService) {
        this.referenceDataValueService = referenceDataValueService;
    }

    @ApiOperation(value = "参考数据值列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                ReferenceDataValueDTO referenceDataValueDTO, @ApiIgnore @SortDefault(value = ReferenceDataValue.FIELD_VALUE_SEQ,
            direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<ReferenceDataValueDTO> list = referenceDataValueService.list(projectId, tenantId, referenceDataValueDTO, pageRequest);
        return Results.success(list);
    }

    @ApiOperation(value = "创建参考数据值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody ReferenceDataValueDTO referenceDataValueDTO) {
        referenceDataValueDTO.setTenantId(tenantId);
        referenceDataValueDTO.setProjectId(projectId);
        this.validObject(referenceDataValueDTO);
        referenceDataValueService.create(projectId, tenantId, referenceDataValueDTO);
        return Results.success(referenceDataValueDTO);
    }

    @ApiOperation(value = "批量创建参考数据值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch")
    public ResponseEntity<?> batchCreate(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody List<ReferenceDataValueDTO> referenceDataValueDTOList) {
        referenceDataValueService.batchCreate(projectId, tenantId, referenceDataValueDTOList);
        return Results.success(referenceDataValueDTOList);
    }

    @ApiOperation(value = "修改参考数据值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody ReferenceDataValueDTO referenceDataValueDTO) {
        referenceDataValueService.update(projectId, tenantId, referenceDataValueDTO);
        return Results.success(referenceDataValueDTO);
    }

    @ApiOperation(value = "批量修改参考数据值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/batch")
    public ResponseEntity<?> batchUpdate(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody List<ReferenceDataValueDTO> referenceDataValueDTOList) {
        referenceDataValueService.batchUpdate(projectId, tenantId, referenceDataValueDTOList);
        return Results.success(referenceDataValueDTOList);
    }

    @ApiOperation(value = "删除参考数据值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody ReferenceDataValueDTO referenceDataValueDTO) {
        referenceDataValueService.remove(projectId, tenantId, referenceDataValueDTO);
        return Results.success();
    }

    @ApiOperation(value = "批量删除参考数据值")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchRemove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody List<ReferenceDataValueDTO> referenceDataValueDTOList) {
        referenceDataValueService.batchRemove(projectId, tenantId, referenceDataValueDTOList);
        return Results.success();
    }
}
