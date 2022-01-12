package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.StandardAimRelationDTO;
import com.hand.hdsp.quality.domain.entity.StandardAimRelation;
import com.hand.hdsp.quality.domain.repository.StandardAimRelationRepository;
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
 * <p>标准落标关系表 管理 API</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@RestController("stanardAimRelationController.v1")
@RequestMapping("/v1/{organizationId}/stanard-aim-relations")
public class StanardAimRelationController extends BaseController {

    private StandardAimRelationRepository stanardAimRelationRepository;

    public StanardAimRelationController(StandardAimRelationRepository stanardAimRelationRepository) {
        this.stanardAimRelationRepository = stanardAimRelationRepository;
    }

    @ApiOperation(value = "标准落标关系表列表")
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
                                  StandardAimRelationDTO standardAimRelationDTO, @ApiIgnore @SortDefault(value = StandardAimRelation.FIELD_RELATION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardAimRelationDTO.setTenantId(tenantId);
        standardAimRelationDTO.setProjectId(HdspConstant.DEFAULT_PROJECT_ID);
        Page<StandardAimRelationDTO> list = stanardAimRelationRepository.pageAndSortDTO(pageRequest, standardAimRelationDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准落标关系表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "relationId",
            value = "标准落标关系表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{relationId}")
    public ResponseEntity<?> detail(@PathVariable Long relationId) {
        StandardAimRelationDTO standardAimRelationDTO = stanardAimRelationRepository.selectDTOByPrimaryKeyAndTenant(relationId);
        return Results.success(standardAimRelationDTO);
    }

    @ApiOperation(value = "创建标准落标关系表")
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
                                    @RequestBody StandardAimRelationDTO standardAimRelationDTO) {
        standardAimRelationDTO.setTenantId(tenantId);
        standardAimRelationDTO.setProjectId(HdspConstant.DEFAULT_PROJECT_ID);
        this.validObject(standardAimRelationDTO);
        stanardAimRelationRepository.insertDTOSelective(standardAimRelationDTO);
        return Results.success(standardAimRelationDTO);
    }

    @ApiOperation(value = "修改标准落标关系表")
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
                                    @RequestBody StandardAimRelationDTO standardAimRelationDTO) {
        standardAimRelationDTO.setProjectId(HdspConstant.DEFAULT_PROJECT_ID);
        stanardAimRelationRepository.updateDTOWhereTenant(standardAimRelationDTO, tenantId);
        return Results.success(standardAimRelationDTO);
    }

    @ApiOperation(value = "删除标准落标关系表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StandardAimRelationDTO standardAimRelationDTO) {
        standardAimRelationDTO.setTenantId(tenantId);
        stanardAimRelationRepository.deleteByPrimaryKey(standardAimRelationDTO);
        return Results.success();
    }
}
