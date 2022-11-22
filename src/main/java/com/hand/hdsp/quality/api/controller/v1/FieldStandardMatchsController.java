package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.app.service.FieldStandardMatchsService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatchs;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchsDTO;
import com.hand.hdsp.quality.domain.repository.FieldStandardMatchsRepository;
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
 * <p>字段标准匹配记录表 管理 API</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
@RestController("fieldStandardMatchsController.v1")
@RequestMapping("/v1/{organizationId}/field-standard-matchss")
public class FieldStandardMatchsController extends BaseController {

    private final FieldStandardMatchsRepository fieldStandardMatchsRepository;
    private final FieldStandardMatchsService fieldStandardMatchsService;

    public FieldStandardMatchsController(FieldStandardMatchsRepository fieldStandardMatchsRepository,
                                         FieldStandardMatchsService fieldStandardMatchsService) {
        this.fieldStandardMatchsRepository = fieldStandardMatchsRepository;
        this.fieldStandardMatchsService = fieldStandardMatchsService;
    }

    @ApiOperation(value = "分页查询字段标准匹配记录列表")
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
                                  FieldStandardMatchsDTO fieldStandardMatchsDTO,
                                  @ApiIgnore @SortDefault(value = FieldStandardMatchs.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        fieldStandardMatchsDTO.setTenantId(tenantId);
        fieldStandardMatchsDTO.setProjectId(projectId);
        Page<FieldStandardMatchsDTO> list = fieldStandardMatchsService.pageAll(pageRequest,fieldStandardMatchsDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "字段标准匹配记录表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "id",
            value = "字段标准匹配记录表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId) {
        FieldStandardMatchsDTO fieldStandardMatchsDTO = fieldStandardMatchsRepository.selectDTOByPrimaryKeyAndTenant(id);
        return Results.success(fieldStandardMatchsDTO);
    }

    @ApiOperation(value = "创建字段标准匹配记录表")
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
                                    @RequestBody FieldStandardMatchsDTO fieldStandardMatchsDTO) {
        fieldStandardMatchsDTO.setTenantId(tenantId);
        fieldStandardMatchsDTO.setProjectId(projectId);
        this.validObject(fieldStandardMatchsDTO);
        fieldStandardMatchsRepository.insertDTOSelective(fieldStandardMatchsDTO);
        return Results.success(fieldStandardMatchsDTO);
    }

    @ApiOperation(value = "修改字段标准匹配记录表")
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
                                    @RequestBody FieldStandardMatchsDTO fieldStandardMatchsDTO) {
        fieldStandardMatchsDTO.setProjectId(projectId);
        fieldStandardMatchsRepository.updateDTOWhereTenant(fieldStandardMatchsDTO, tenantId);
        return Results.success(fieldStandardMatchsDTO);
    }

    @ApiOperation(value = "删除字段标准匹配记录表")
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
                                    @RequestBody FieldStandardMatchsDTO fieldStandardMatchsDTO) {
        fieldStandardMatchsDTO.setTenantId(tenantId);
        fieldStandardMatchsDTO.setProjectId(projectId);
        fieldStandardMatchsRepository.deleteByPrimaryKey(fieldStandardMatchsDTO);
        return Results.success();
    }
}
