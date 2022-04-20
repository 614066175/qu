package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.app.service.LovValueVersionService;
import com.hand.hdsp.quality.app.service.LovVersionService;
import com.hand.hdsp.quality.domain.entity.LovVersion;
import com.hand.hdsp.quality.domain.repository.LovVersionRepository;
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

import java.util.List;

/**
 * <p>LOV表 管理 API</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@RestController("lovVersionController.v1")
@RequestMapping("/v1/{organizationId}/lov-versions")
public class LovVersionController extends BaseController {

    private LovVersionRepository lovVersionRepository;
    private LovVersionService lovVersionService;

    public LovVersionController(LovVersionRepository lovVersionRepository, LovVersionService lovVersionService) {
        this.lovVersionRepository = lovVersionRepository;
        this.lovVersionService=lovVersionService;
    }

    @ApiOperation(value = "LOV表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                LovVersionDTO lovVersionDTO, @ApiIgnore @SortDefault(value = LovVersion.FIELD_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        lovVersionDTO.setTenantId(tenantId);
        Page<LovVersionDTO> list = lovVersionRepository.pageAndSortDTO(pageRequest, lovVersionDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "LOV表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "versionId",
            value = "LOV表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{versionId}")
    public ResponseEntity<?> detail(@PathVariable Long versionId) {
        LovVersionDTO lovVersionDTO = lovVersionRepository.selectDTOByPrimaryKeyAndTenant(versionId);
        return Results.success(lovVersionDTO);
    }

    @ApiOperation(value = "创建LOV表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody LovVersionDTO lovVersionDTO) {
        lovVersionDTO.setTenantId(tenantId);
        this.validObject(lovVersionDTO);
        lovVersionRepository.insertDTOSelective(lovVersionDTO);
        return Results.success(lovVersionDTO);
    }

    @ApiOperation(value = "修改LOV表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody LovVersionDTO lovVersionDTO) {
                lovVersionRepository.updateDTOWhereTenant(lovVersionDTO, tenantId);
        return Results.success(lovVersionDTO);
    }

    @ApiOperation(value = "删除LOV表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody LovVersionDTO lovVersionDTO) {
                lovVersionDTO.setTenantId(tenantId);
        lovVersionRepository.deleteByPrimaryKey(lovVersionDTO);
        return Results.success();
    }


    @ApiOperation(value = "代码集历史版本")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/history/version")
    public ResponseEntity<List<CodeVersion>> version(@PathVariable(name = "organizationId") Long tenantId,
                                                     Long lovId, @ApiIgnore @SortDefault(value = LovVersion.FIELD_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        List<CodeVersion> lovVersion=  lovVersionService.getVersion(lovId);

        return Results.success(lovVersion);
    }

    // TODO: 2022-04-20

    @ApiOperation(value = "代码集历史版本查看")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/history/watch")
    public ResponseEntity<List<CodeVersion>> watch(@PathVariable(name = "organizationId") Long tenantId,
                                                     Long lovId, @ApiIgnore @SortDefault(value = LovVersion.FIELD_VERSION_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {


        return null;
    }


}
