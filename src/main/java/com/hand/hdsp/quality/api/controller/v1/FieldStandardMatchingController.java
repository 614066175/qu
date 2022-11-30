package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.app.service.FieldStandardMatchingService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatching;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;
import com.hand.hdsp.quality.domain.repository.FieldStandardMatchingRepository;
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
 * <p>字段标准匹配表 管理 API</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@RestController("fieldStandardMatchingController.v1")
@RequestMapping("/v1/{organizationId}/field-standard-matchings")
public class FieldStandardMatchingController extends BaseController {

    private final FieldStandardMatchingRepository fieldStandardMatchingRepository;
    private final FieldStandardMatchingService fieldStandardMatchingService;

    public FieldStandardMatchingController(FieldStandardMatchingRepository fieldStandardMatchingRepository,
                                           FieldStandardMatchingService fieldStandardMatchingService) {
        this.fieldStandardMatchingRepository = fieldStandardMatchingRepository;
        this.fieldStandardMatchingService = fieldStandardMatchingService;
    }

    @ApiOperation(value = "字段标准匹配表列表")
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
                FieldStandardMatchingDTO fieldStandardMatchingDTO, @ApiIgnore @SortDefault(value = FieldStandardMatching.FIELD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        fieldStandardMatchingDTO.setTenantId(tenantId);
        fieldStandardMatchingDTO.setProjectId(projectId);
        Page<FieldStandardMatchingDTO> list = fieldStandardMatchingService.pageFieldStandardMatching(pageRequest,fieldStandardMatchingDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "字段标准匹配表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "id",
            value = "字段标准匹配表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        FieldStandardMatchingDTO fieldStandardMatchingDTO = fieldStandardMatchingRepository.selectDTOByPrimaryKeyAndTenant(id);
        return Results.success(fieldStandardMatchingDTO);
    }

    @ApiOperation(value = "创建字段标准匹配表")
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
                                    @RequestBody FieldStandardMatchingDTO fieldStandardMatchingDTO) {
        fieldStandardMatchingDTO.setTenantId(tenantId);
        fieldStandardMatchingDTO.setProjectId(projectId);
        this.validObject(fieldStandardMatchingDTO);
        fieldStandardMatchingRepository.insertDTOSelective(fieldStandardMatchingDTO);
        return Results.success(fieldStandardMatchingDTO);
    }

    @ApiOperation(value = "修改字段标准匹配表")
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
                                    @RequestBody FieldStandardMatchingDTO fieldStandardMatchingDTO) {
        fieldStandardMatchingDTO.setProjectId(projectId);
        fieldStandardMatchingRepository.updateDTOWhereTenant(fieldStandardMatchingDTO, tenantId);
        return Results.success(fieldStandardMatchingDTO);
    }

    @ApiOperation(value = "删除字段标准匹配表")
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
                                    @RequestBody FieldStandardMatchingDTO fieldStandardMatchingDTO) {
        fieldStandardMatchingDTO.setTenantId(tenantId);
        fieldStandardMatchingDTO.setProjectId(projectId);
        fieldStandardMatchingRepository.deleteByPrimaryKey(fieldStandardMatchingDTO);
        return Results.success();
    }
}
