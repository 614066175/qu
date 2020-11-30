package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameExecHisDetail;
import com.hand.hdsp.quality.api.dto.NameExecHisDetailDTO;
import com.hand.hdsp.quality.domain.repository.NameExecHisDetailRepository;
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
 * <p>命名标准执行历史详情表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameExecHisDetailController.v1")
@RequestMapping("/v1/{organizationId}/name-exec-his-details")
public class NameExecHisDetailController extends BaseController {

    private NameExecHisDetailRepository nameExecHisDetailRepository;

    public NameExecHisDetailController(NameExecHisDetailRepository nameExecHisDetailRepository) {
        this.nameExecHisDetailRepository = nameExecHisDetailRepository;
    }

    @ApiOperation(value = "命名标准执行历史详情表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameExecHisDetailDTO nameExecHisDetailDTO, @ApiIgnore @SortDefault(value = NameExecHisDetail.FIELD_DETAIL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameExecHisDetailDTO.setTenantId(tenantId);
        Page<NameExecHisDetailDTO> list = nameExecHisDetailRepository.pageAndSortDTO(pageRequest, nameExecHisDetailDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准执行历史详情表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "detailId",
            value = "命名标准执行历史详情表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{detailId}")
    public ResponseEntity<?> detail(@PathVariable Long detailId) {
        NameExecHisDetailDTO nameExecHisDetailDTO = nameExecHisDetailRepository.selectDTOByPrimaryKeyAndTenant(detailId);
        return Results.success(nameExecHisDetailDTO);
    }

    @ApiOperation(value = "创建命名标准执行历史详情表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameExecHisDetailDTO nameExecHisDetailDTO) {
        nameExecHisDetailDTO.setTenantId(tenantId);
        this.validObject(nameExecHisDetailDTO);
        nameExecHisDetailRepository.insertDTOSelective(nameExecHisDetailDTO);
        return Results.success(nameExecHisDetailDTO);
    }

    @ApiOperation(value = "修改命名标准执行历史详情表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameExecHisDetailDTO nameExecHisDetailDTO) {
                nameExecHisDetailRepository.updateDTOWhereTenant(nameExecHisDetailDTO, tenantId);
        return Results.success(nameExecHisDetailDTO);
    }

    @ApiOperation(value = "删除命名标准执行历史详情表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameExecHisDetailDTO nameExecHisDetailDTO) {
                nameExecHisDetailDTO.setTenantId(tenantId);
        nameExecHisDetailRepository.deleteByPrimaryKey(nameExecHisDetailDTO);
        return Results.success();
    }
}