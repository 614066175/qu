package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.StanardAimRelationDTO;
import com.hand.hdsp.quality.domain.entity.StanardAimRelation;
import com.hand.hdsp.quality.domain.repository.StanardAimRelationRepository;
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

    private StanardAimRelationRepository stanardAimRelationRepository;

    public StanardAimRelationController(StanardAimRelationRepository stanardAimRelationRepository) {
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
                                  StanardAimRelationDTO stanardAimRelationDTO, @ApiIgnore @SortDefault(value = StanardAimRelation.FIELD_RELATION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        stanardAimRelationDTO.setTenantId(tenantId);
        Page<StanardAimRelationDTO> list = stanardAimRelationRepository.pageAndSortDTO(pageRequest, stanardAimRelationDTO);
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
        StanardAimRelationDTO stanardAimRelationDTO = stanardAimRelationRepository.selectDTOByPrimaryKeyAndTenant(relationId);
        return Results.success(stanardAimRelationDTO);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody StanardAimRelationDTO stanardAimRelationDTO) {
        stanardAimRelationDTO.setTenantId(tenantId);
        this.validObject(stanardAimRelationDTO);
        stanardAimRelationRepository.insertDTOSelective(stanardAimRelationDTO);
        return Results.success(stanardAimRelationDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StanardAimRelationDTO stanardAimRelationDTO) {
                stanardAimRelationRepository.updateDTOWhereTenant(stanardAimRelationDTO, tenantId);
        return Results.success(stanardAimRelationDTO);
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
                                    @RequestBody StanardAimRelationDTO stanardAimRelationDTO) {
                stanardAimRelationDTO.setTenantId(tenantId);
        stanardAimRelationRepository.deleteByPrimaryKey(stanardAimRelationDTO);
        return Results.success();
    }
}
