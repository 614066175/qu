package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.StandardApproval;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.domain.repository.StandardApprovalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

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
 * <p>各种标准审批表 管理 API</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@RestController("standardApprovalController.v1")
@RequestMapping("/v1/{organizationId}/standard-approvals")
public class StandardApprovalController extends BaseController {

    private StandardApprovalRepository standardApprovalRepository;

    public StandardApprovalController(StandardApprovalRepository standardApprovalRepository) {
        this.standardApprovalRepository = standardApprovalRepository;
    }

    @ApiOperation(value = "各种标准审批表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                StandardApprovalDTO standardApprovalDTO, @ApiIgnore @SortDefault(value = StandardApproval.FIELD_APPROVAL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardApprovalDTO.setTenantId(tenantId);
        Page<StandardApprovalDTO> list = standardApprovalRepository.pageAndSortDTO(pageRequest, standardApprovalDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "各种标准审批表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "approvalId",
            value = "各种标准审批表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{approvalId}")
    public ResponseEntity<?> detail(@PathVariable Long approvalId) {
        StandardApprovalDTO standardApprovalDTO = standardApprovalRepository.selectDTOByPrimaryKeyAndTenant(approvalId);
        return Results.success(standardApprovalDTO);
    }

    @ApiOperation(value = "创建各种标准审批表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody StandardApprovalDTO standardApprovalDTO) {
        standardApprovalDTO.setTenantId(tenantId);
        this.validObject(standardApprovalDTO);
        standardApprovalRepository.insertDTOSelective(standardApprovalDTO);
        return Results.success(standardApprovalDTO);
    }

    @ApiOperation(value = "修改各种标准审批表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StandardApprovalDTO standardApprovalDTO) {
                standardApprovalRepository.updateDTOWhereTenant(standardApprovalDTO, tenantId);
        return Results.success(standardApprovalDTO);
    }

    @ApiOperation(value = "删除各种标准审批表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody StandardApprovalDTO standardApprovalDTO) {
                standardApprovalDTO.setTenantId(tenantId);
        standardApprovalRepository.deleteByPrimaryKey(standardApprovalDTO);
        return Results.success();
    }
}
