package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.NameStandardContentHeadDTO;
import com.hand.hdsp.quality.domain.entity.NameStandardContentHead;
import com.hand.hdsp.quality.domain.repository.NameStandardContentHeadRepository;
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
 * <p>命名标准落标头表 管理 API</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@RestController("nameStandardContentHeadController.v1")
@RequestMapping("/v1/{organizationId}/name-standard-content-heads")
public class NameStandardContentHeadController extends BaseController {

    private NameStandardContentHeadRepository nameStandardContentHeadRepository;

    public NameStandardContentHeadController(NameStandardContentHeadRepository nameStandardContentHeadRepository) {
        this.nameStandardContentHeadRepository = nameStandardContentHeadRepository;
    }

    @ApiOperation(value = "命名标准落标头表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                NameStandardContentHeadDTO nameStandardContentHeadDTO, @ApiIgnore @SortDefault(value = NameStandardContentHead.FIELD_CONTENT_HEAD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameStandardContentHeadDTO.setTenantId(tenantId);
        Page<NameStandardContentHeadDTO> list = nameStandardContentHeadRepository.pageAndSortDTO(pageRequest, nameStandardContentHeadDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准落标头表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "contentHeadId",
            value = "命名标准落标头表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{contentHeadId}")
    public ResponseEntity<?> detail(@PathVariable Long contentHeadId) {
        NameStandardContentHeadDTO nameStandardContentHeadDTO = nameStandardContentHeadRepository.selectDTOByPrimaryKeyAndTenant(contentHeadId);
        return Results.success(nameStandardContentHeadDTO);
    }

    @ApiOperation(value = "创建命名标准落标头表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardContentHeadDTO nameStandardContentHeadDTO) {
        nameStandardContentHeadDTO.setTenantId(tenantId);
        this.validObject(nameStandardContentHeadDTO);
        nameStandardContentHeadRepository.insertDTOSelective(nameStandardContentHeadDTO);
        return Results.success(nameStandardContentHeadDTO);
    }

    @ApiOperation(value = "修改命名标准落标头表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody NameStandardContentHeadDTO nameStandardContentHeadDTO) {
                nameStandardContentHeadRepository.updateDTOWhereTenant(nameStandardContentHeadDTO, tenantId);
        return Results.success(nameStandardContentHeadDTO);
    }

    @ApiOperation(value = "删除命名标准落标头表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody NameStandardContentHeadDTO nameStandardContentHeadDTO) {
                nameStandardContentHeadDTO.setTenantId(tenantId);
        nameStandardContentHeadRepository.deleteByPrimaryKey(nameStandardContentHeadDTO);
        return Results.success();
    }
}
