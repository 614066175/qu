package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.StandardApproveDTO;
import com.hand.hdsp.quality.domain.entity.StandardApprove;
import com.hand.hdsp.quality.domain.repository.StandardApproveRepository;
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
 * <p>标准申请记录表 管理 API</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 16:49:46
 */
@RestController("standardApproveController.v1")
@RequestMapping("/v1/{organizationId}/standard-approves")
public class StandardApproveController extends BaseController {

    private StandardApproveRepository standardApproveRepository;

    public StandardApproveController(StandardApproveRepository standardApproveRepository) {
        this.standardApproveRepository = standardApproveRepository;
    }

    @ApiOperation(value = "标准申请记录表列表")
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
                StandardApproveDTO standardApproveDTO, @ApiIgnore @SortDefault(value = StandardApprove.FIELD_APPROVE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardApproveDTO.setTenantId(tenantId);
        standardApproveDTO.setProjectId(projectId);
        Page<StandardApproveDTO> list = standardApproveRepository.pageAndSortDTO(pageRequest, standardApproveDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准申请记录表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "approveId",
            value = "标准申请记录表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{approveId}")
    public ResponseEntity<?> detail(@PathVariable Long approveId) {
        StandardApproveDTO standardApproveDTO = standardApproveRepository.selectDTOByPrimaryKeyAndTenant(approveId);
        return Results.success(standardApproveDTO);
    }

    @ApiOperation(value = "创建标准申请记录表")
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
                                    @RequestBody StandardApproveDTO standardApproveDTO) {
        standardApproveDTO.setTenantId(tenantId);
        standardApproveDTO.setProjectId(projectId);
        this.validObject(standardApproveDTO);
        standardApproveRepository.insertDTOSelective(standardApproveDTO);
        return Results.success(standardApproveDTO);
    }

    @ApiOperation(value = "修改标准申请记录表")
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
                                    @RequestBody StandardApproveDTO standardApproveDTO) {
        standardApproveDTO.setProjectId(projectId);
        standardApproveRepository.updateDTOWhereTenant(standardApproveDTO, tenantId);
        return Results.success(standardApproveDTO);
    }

    @ApiOperation(value = "删除标准申请记录表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StandardApproveDTO standardApproveDTO) {
                standardApproveDTO.setTenantId(tenantId);
        standardApproveRepository.deleteByPrimaryKey(standardApproveDTO);
        return Results.success();
    }
}
