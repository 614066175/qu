package com.hand.hdsp.quality.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.app.service.ReferenceDataService;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
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
 * <p>参考数据头表 管理 API</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@RestController("referenceDataController.v1")
@RequestMapping("/v1/{organizationId}/reference-data")
public class ReferenceDataController extends BaseController {

    private final ReferenceDataService referenceDataService;

    public ReferenceDataController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @ApiOperation(value = "参考数据头表列表")
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
                                  ReferenceDataDTO referenceDataDTO,
                                  @ApiIgnore @SortDefault(value = ReferenceData.FIELD_DATA_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<ReferenceDataDTO> list = referenceDataService.list(projectId, tenantId, referenceDataDTO, pageRequest);
        return Results.success(list);
    }

    @ApiOperation(value = "参考数据头表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "dataId",
            value = "参考数据头表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dataId}")
    public ResponseEntity<?> detail(@PathVariable Long dataId) {
        ReferenceDataDTO referenceDataDTO = referenceDataService.detail(dataId);
        return Results.success(referenceDataDTO);
    }

    @ApiOperation(value = "创建参考数据头表")
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
                                    @RequestBody ReferenceDataDTO referenceDataDTO) {
        referenceDataDTO.setTenantId(tenantId);
        referenceDataDTO.setProjectId(projectId);
        this.validObject(referenceDataDTO);
        referenceDataService.create(projectId, tenantId, referenceDataDTO);
        return Results.success(referenceDataDTO);
    }

    @ApiOperation(value = "修改参考数据头表")
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
                                    @RequestBody ReferenceDataDTO referenceDataDTO) {
        referenceDataService.update(projectId, tenantId, referenceDataDTO);
        return Results.success(referenceDataDTO);
    }


    @ApiOperation(value = "删除参考数据头表")
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
                                    @RequestBody ReferenceDataDTO referenceDataDTO) {
        referenceDataService.remove(projectId, tenantId, referenceDataDTO);
        return Results.success();
    }

    @ApiOperation(value = "批量删除参考数据头表")
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
                                    @RequestBody List<ReferenceDataDTO> referenceDataDTOList) {
        referenceDataService.batchRemove(projectId, tenantId, referenceDataDTOList);
        return Results.success();
    }

    @ApiOperation(value = "发布参考数据")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/release")
    public ResponseEntity<?> release(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                         @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                         @RequestBody ReferenceDataDTO referenceDataDTO) {
        referenceDataService.release(projectId, tenantId, referenceDataDTO);
        return Results.success();
    }


    @ApiOperation(value = "批量发布参考数据")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch/release")
    public ResponseEntity<?> batchRelease(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                     @RequestBody List<ReferenceDataDTO> referenceDataDTOList) {
        referenceDataService.batchRelease(projectId, tenantId, referenceDataDTOList);
        return Results.success();
    }


    @ApiOperation(value = "下线参考数据")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/offline")
    public ResponseEntity<?> offline(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                     @RequestBody ReferenceDataDTO referenceDataDTO) {
        referenceDataService.offline(projectId, tenantId, referenceDataDTO);
        return Results.success();
    }


    @ApiOperation(value = "批量下线参考数据")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch/offline")
    public ResponseEntity<?> batchOffline(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                          @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                          @RequestBody List<ReferenceDataDTO> referenceDataDTOList) {
        referenceDataService.batchOffline(projectId, tenantId, referenceDataDTOList);
        return Results.success();
    }

    @ApiOperation(value = "发布参考数据的回调接口-工作流使用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/release-callback")
    public ResponseEntity<?> releaseCallback(@PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam Long recordId,
                                     @RequestParam String nodeApproveResult) {
        referenceDataService.releaseCallback(tenantId, recordId, nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "下线参考数据的回调接口-工作流使用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/offline-callback")
    public ResponseEntity<?> offlineCallback(@PathVariable(name = "organizationId") Long tenantId,
                                             @RequestParam Long recordId,
                                             @RequestParam String nodeApproveResult) {
        referenceDataService.offlineCallback(tenantId, recordId, nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "工作流撤回接口-工作流使用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/withdraw-event")
    public ResponseEntity<?> withdrawEvent(@PathVariable(name = "organizationId") Long tenantId,
                                             @RequestParam Long recordId) {
        referenceDataService.withdrawEvent(tenantId, recordId);
        return Results.success();
    }


}
