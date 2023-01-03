package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.app.service.RootMatchHisService;
import com.hand.hdsp.quality.app.service.RootMatchService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.RootMatchHis;
import com.hand.hdsp.quality.api.dto.RootMatchHisDTO;
import com.hand.hdsp.quality.domain.repository.RootMatchHisRepository;
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
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@RestController("rootMatchHisController.v1")
@RequestMapping("/v1/{organizationId}/root-match-hiss")
public class RootMatchHisController extends BaseController {

    private final RootMatchHisRepository rootMatchHisRepository;
    private final RootMatchHisService rootMatchHisService;

    public RootMatchHisController(RootMatchHisRepository rootMatchHisRepository,
                                  RootMatchHisService rootMatchHisService) {
        this.rootMatchHisRepository = rootMatchHisRepository;
        this.rootMatchHisService = rootMatchHisService;
    }

    @ApiOperation(value = "字段标准匹配记录表列表")
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
                                  RootMatchHisDTO rootMatchHisDTO,
                                  @ApiIgnore @SortDefault(value = RootMatchHis.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        rootMatchHisDTO.setTenantId(tenantId);
        rootMatchHisDTO.setProjectId(projectId);
        Page<RootMatchHisDTO> list = rootMatchHisService.pageAll(pageRequest,rootMatchHisDTO);
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
    public ResponseEntity<?> detail(@PathVariable Long id) {
        RootMatchHisDTO rootMatchHisDTO = rootMatchHisRepository.selectDTOByPrimaryKeyAndTenant(id);
        return Results.success(rootMatchHisDTO);
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
                                    @RequestBody RootMatchHisDTO rootMatchHisDTO) {
        rootMatchHisDTO.setTenantId(tenantId);
        rootMatchHisDTO.setProjectId(projectId);
        this.validObject(rootMatchHisDTO);
        rootMatchHisRepository.insertDTOSelective(rootMatchHisDTO);
        return Results.success(rootMatchHisDTO);
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
                                    @RequestBody RootMatchHisDTO rootMatchHisDTO) {
        rootMatchHisRepository.updateDTOWhereTenant(rootMatchHisDTO, tenantId);
        return Results.success(rootMatchHisDTO);
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
                                    @RequestBody RootMatchHisDTO rootMatchHisDTO) {
        rootMatchHisDTO.setTenantId(tenantId);
        rootMatchHisDTO.setProjectId(projectId);
        rootMatchHisRepository.deleteByPrimaryKey(rootMatchHisDTO);
        return Results.success();
    }
}
