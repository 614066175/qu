package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.HdspConstant;
import org.xdsp.quality.api.dto.ProblemDTO;
import org.xdsp.quality.app.service.ProblemService;
import org.xdsp.quality.domain.repository.ProblemRepository;
import org.xdsp.quality.infra.vo.ProblemVO;

/**
 * <p>问题库表 管理 API</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@RestController("problemController.v1")
@RequestMapping("/v1/{organizationId}/problems")
public class ProblemController extends BaseController {

    private final ProblemRepository problemRepository;
    private final ProblemService problemService;

    public ProblemController(ProblemRepository problemRepository, ProblemService problemService) {
        this.problemRepository = problemRepository;
        this.problemService = problemService;
    }

    @ApiOperation(value = "问题库表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/tree")
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  ProblemVO problemVO) {
        problemVO.setTenantId(tenantId);
        problemVO.setProjectId(projectId);
        return Results.success(problemService.listForTree(problemVO));
    }

    @ApiOperation(value = "问题库表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "problemId",
            value = "问题库表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{problemId}")
    public ResponseEntity<?> detail(@PathVariable Long problemId) {

        return Results.success(problemService.detail(problemId));
    }

    @ApiOperation(value = "创建问题库表")
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
                                    @RequestBody ProblemDTO problemDTO) {
        problemDTO.setTenantId(tenantId);
        problemDTO.setProjectId(projectId);
        this.validObject(problemDTO);
        problemService.create(problemDTO);
//        problemRepository.insertDTOSelective(problemDTO);
        return Results.success(problemDTO);
    }

    @ApiOperation(value = "修改问题库表")
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
                                    @RequestBody ProblemDTO problemDTO) {
        problemDTO.setProjectId(projectId);
        problemRepository.updateDTOAllColumnWhereTenant(problemDTO, tenantId);
        return Results.success(problemDTO);
    }

    @ApiOperation(value = "删除问题库表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody ProblemDTO problemDTO) {
        problemDTO.setTenantId(tenantId);
        problemService.deleteProblem(problemDTO);
        return Results.success();
    }
}
