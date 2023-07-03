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
import org.xdsp.quality.api.dto.SuggestDTO;
import org.xdsp.quality.app.service.SuggestService;
import org.xdsp.quality.domain.entity.Suggest;
import org.xdsp.quality.domain.repository.SuggestRepository;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>问题知识库表 管理 API</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@RestController("suggestController.v1")
@RequestMapping("/v1/{organizationId}/suggests")
public class SuggestController extends BaseController {

    private final SuggestRepository suggestRepository;
    private final SuggestService suggestService;

    public SuggestController(SuggestRepository suggestRepository,
                             SuggestService suggestService) {
        this.suggestRepository = suggestRepository;
        this.suggestService = suggestService;
    }

    @ApiOperation(value = "问题知识库表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  SuggestDTO suggestDTO, @ApiIgnore @SortDefault(value = Suggest.FIELD_SUGGEST_ORDER,
            direction = Sort.Direction.ASC) PageRequest pageRequest) {
        suggestDTO.setTenantId(tenantId);
        suggestDTO.setProjectId(projectId);
        Page<SuggestDTO> list = suggestService.list(pageRequest, suggestDTO);
        return Results.success(list);
    }


    @ApiOperation(value = "问题知识库表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "suggestId",
            value = "问题知识库表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{suggestId}")
    public ResponseEntity<?> detail(@PathVariable Long suggestId) {
        SuggestDTO suggestDTO = suggestRepository.selectDTOByPrimaryKeyAndTenant(suggestId);
        return Results.success(suggestDTO);
    }

    @ApiOperation(value = "创建问题知识库表")
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
                                    @RequestBody SuggestDTO suggestDTO) {
        suggestDTO.setTenantId(tenantId);
        suggestDTO.setProjectId(projectId);
        this.validObject(suggestDTO);
        return Results.success(suggestService.createSuggest(suggestDTO));
    }

    @ApiOperation(value = "修改问题知识库表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody SuggestDTO suggestDTO) {
        suggestDTO.setProjectId(projectId);
        suggestRepository.updateDTOWhereTenant(suggestDTO, tenantId);
        return Results.success(suggestDTO);
    }

    @ApiOperation(value = "删除问题知识库表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody List<SuggestDTO> suggests) {
        suggestRepository.batchDTODeleteByPrimaryKey(suggests);
        return Results.success();
    }
}
