package org.xdsp.quality.api.controller.v1;

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
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.StandardRelationDTO;
import org.xdsp.quality.domain.entity.StandardRelation;
import org.xdsp.quality.domain.repository.StandardRelationRepository;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>标准-标准组关系表 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@RestController("standardRelationController.v1")
@RequestMapping("/v1/{organizationId}/standard-relations")
public class StandardRelationController extends BaseController {

    private StandardRelationRepository standardRelationRepository;

    public StandardRelationController(StandardRelationRepository standardRelationRepository) {
        this.standardRelationRepository = standardRelationRepository;
    }

    @ApiOperation(value = "标准-标准组关系表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  StandardRelationDTO standardRelationDTO, @ApiIgnore @SortDefault(value = StandardRelation.FIELD_RELATION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardRelationDTO.setTenantId(tenantId);
        Page<StandardRelationDTO> list = standardRelationRepository.pageAndSortDTO(pageRequest, standardRelationDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准-标准组关系表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "relationId",
            value = "标准-标准组关系表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{relationId}")
    public ResponseEntity<?> detail(@PathVariable Long relationId) {
        StandardRelationDTO standardRelationDTO = standardRelationRepository.selectDTOByPrimaryKeyAndTenant(relationId);
        return Results.success(standardRelationDTO);
    }

    @ApiOperation(value = "创建标准-标准组关系表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody List<StandardRelationDTO> standardRelationDTOList) {
        standardRelationDTOList.forEach(standardRelationDTO -> {
            standardRelationDTO.setTenantId(tenantId);
            standardRelationDTO.setProjectId(projectId);
        });
        standardRelationRepository.batchInsertDTOSelective(standardRelationDTOList);
        return Results.success(standardRelationDTOList);
    }

    @ApiOperation(value = "修改标准-标准组关系表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StandardRelationDTO standardRelationDTO) {
        standardRelationRepository.updateDTOWhereTenant(standardRelationDTO, tenantId);
        return Results.success(standardRelationDTO);
    }

    @ApiOperation(value = "删除标准-标准组关系表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody List<StandardRelationDTO> standardRelationDTOS) {
        standardRelationRepository.batchDTODeleteByPrimaryKey(standardRelationDTOS);
        return Results.success();
    }
}
