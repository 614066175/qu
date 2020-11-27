package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.NameStandardHistoryHeadDTO;
import com.hand.hdsp.quality.domain.entity.NameStandardHistoryHead;
import com.hand.hdsp.quality.domain.repository.NameStandardHistoryHeadRepository;
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
 * <p>命名标准执行历史头表 管理 API</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@RestController("nameStandardHistoryHeadController.v1")
@RequestMapping("/v1/{organizationId}/name-standard-history-heads")
public class NameStandardHistoryHeadController extends BaseController {

    private NameStandardHistoryHeadRepository nameStandardHistoryHeadRepository;

    public NameStandardHistoryHeadController(NameStandardHistoryHeadRepository nameStandardHistoryHeadRepository) {
        this.nameStandardHistoryHeadRepository = nameStandardHistoryHeadRepository;
    }

    @ApiOperation(value = "命名标准执行历史头表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameStandardHistoryHeadDTO nameStandardHistoryHeadDTO, @ApiIgnore @SortDefault(value = NameStandardHistoryHead.FIELD_HISTORY_HEAD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameStandardHistoryHeadDTO.setTenantId(tenantId);
        Page<NameStandardHistoryHeadDTO> list = nameStandardHistoryHeadRepository.pageAndSortDTO(pageRequest, nameStandardHistoryHeadDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准执行历史头表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "historyHeadId",
            value = "命名标准执行历史头表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{historyHeadId}")
    public ResponseEntity<?> detail(@PathVariable Long historyHeadId) {
        NameStandardHistoryHeadDTO nameStandardHistoryHeadDTO = nameStandardHistoryHeadRepository.selectDTOByPrimaryKeyAndTenant(historyHeadId);
        return Results.success(nameStandardHistoryHeadDTO);
    }

    @ApiOperation(value = "创建命名标准执行历史头表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardHistoryHeadDTO nameStandardHistoryHeadDTO) {
        nameStandardHistoryHeadDTO.setTenantId(tenantId);
        this.validObject(nameStandardHistoryHeadDTO);
        nameStandardHistoryHeadRepository.insertDTOSelective(nameStandardHistoryHeadDTO);
        return Results.success(nameStandardHistoryHeadDTO);
    }

    @ApiOperation(value = "修改命名标准执行历史头表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardHistoryHeadDTO nameStandardHistoryHeadDTO) {
                nameStandardHistoryHeadRepository.updateDTOWhereTenant(nameStandardHistoryHeadDTO, tenantId);
        return Results.success(nameStandardHistoryHeadDTO);
    }

    @ApiOperation(value = "删除命名标准执行历史头表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameStandardHistoryHeadDTO nameStandardHistoryHeadDTO) {
                nameStandardHistoryHeadDTO.setTenantId(tenantId);
        nameStandardHistoryHeadRepository.deleteByPrimaryKey(nameStandardHistoryHeadDTO);
        return Results.success();
    }
}
