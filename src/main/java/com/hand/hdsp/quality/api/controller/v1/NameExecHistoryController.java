package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.api.dto.NameExecHistoryDTO;
import com.hand.hdsp.quality.domain.repository.NameExecHistoryRepository;
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
 * <p>命名标准执行历史表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameExecHistoryController.v1")
@RequestMapping("/v1/{organizationId}/name-exec-historys")
public class NameExecHistoryController extends BaseController {

    private NameExecHistoryRepository nameExecHistoryRepository;

    public NameExecHistoryController(NameExecHistoryRepository nameExecHistoryRepository) {
        this.nameExecHistoryRepository = nameExecHistoryRepository;
    }

    @ApiOperation(value = "命名标准执行历史表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameExecHistoryDTO nameExecHistoryDTO, @ApiIgnore @SortDefault(value = NameExecHistory.FIELD_HISTORY_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameExecHistoryDTO.setTenantId(tenantId);
        Page<NameExecHistoryDTO> list = nameExecHistoryRepository.pageAndSortDTO(pageRequest, nameExecHistoryDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准执行历史表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "historyId",
            value = "命名标准执行历史表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{historyId}")
    public ResponseEntity<?> detail(@PathVariable Long historyId) {
        NameExecHistoryDTO nameExecHistoryDTO = nameExecHistoryRepository.selectDTOByPrimaryKeyAndTenant(historyId);
        return Results.success(nameExecHistoryDTO);
    }

    @ApiOperation(value = "创建命名标准执行历史表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameExecHistoryDTO nameExecHistoryDTO) {
        nameExecHistoryDTO.setTenantId(tenantId);
        this.validObject(nameExecHistoryDTO);
        nameExecHistoryRepository.insertDTOSelective(nameExecHistoryDTO);
        return Results.success(nameExecHistoryDTO);
    }

    @ApiOperation(value = "修改命名标准执行历史表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameExecHistoryDTO nameExecHistoryDTO) {
                nameExecHistoryRepository.updateDTOWhereTenant(nameExecHistoryDTO, tenantId);
        return Results.success(nameExecHistoryDTO);
    }

    @ApiOperation(value = "删除命名标准执行历史表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameExecHistoryDTO nameExecHistoryDTO) {
                nameExecHistoryDTO.setTenantId(tenantId);
        nameExecHistoryRepository.deleteByPrimaryKey(nameExecHistoryDTO);
        return Results.success();
    }
}
