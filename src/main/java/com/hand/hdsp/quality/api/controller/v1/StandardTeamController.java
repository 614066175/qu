package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.app.service.StandardTeamService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import com.hand.hdsp.quality.domain.repository.StandardTeamRepository;
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

import java.util.List;

/**
 * <p>标准组 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@RestController("standardTeamController.v1")
@RequestMapping("/v1/{organizationId}/standard-teams")
public class StandardTeamController extends BaseController {

    private final StandardTeamRepository standardTeamRepository;
    private final StandardTeamService standardTeamService;

    public StandardTeamController(StandardTeamRepository standardTeamRepository, StandardTeamService standardTeamService) {
        this.standardTeamRepository = standardTeamRepository;
        this.standardTeamService = standardTeamService;
    }

    @ApiOperation(value = "标准组列表")
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
                                  StandardTeamDTO standardTeamDTO, @ApiIgnore @SortDefault(value = StandardTeam.FIELD_STANDARD_TEAM_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        standardTeamDTO.setTenantId(tenantId);
        Page<StandardTeamDTO> list = standardTeamService.list(pageRequest, standardTeamDTO);
        return Results.success(list);
    }


    @ApiOperation(value = "标准组列表查询所有")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> listAll(@PathVariable(name = "organizationId") Long tenantId,
                                     @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                     StandardTeamDTO standardTeamDTO) {
        standardTeamDTO.setTenantId(tenantId);
        List<StandardTeamDTO> list = standardTeamService.listAll(standardTeamDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准组明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = " standardTeamId",
            value = "标准组主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{standardTeamId}")
    public ResponseEntity<?> detail(@PathVariable Long standardTeamId) {
        StandardTeamDTO standardTeamDTO = standardTeamRepository.selectDTOByPrimaryKeyAndTenant(standardTeamId);
        return Results.success(standardTeamDTO);
    }

    @ApiOperation(value = "创建标准组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody StandardTeamDTO standardTeamDTO) {
        standardTeamDTO.setTenantId(tenantId);
        this.validObject(standardTeamDTO);
        standardTeamRepository.insertDTOSelective(standardTeamDTO);
        return Results.success(standardTeamDTO);
    }

    @ApiOperation(value = "修改标准组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody StandardTeamDTO standardTeamDTO) {
        standardTeamRepository.updateDTOWhereTenant(standardTeamDTO, tenantId);
        return Results.success(standardTeamDTO);
    }

    @ApiOperation(value = "删除标准组")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{standardTeamId}")
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @PathVariable Long standardTeamId) {
        standardTeamService.remove(standardTeamId);
        return Results.success(standardTeamId);
    }

}
