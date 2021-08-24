package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.app.service.ProblemService;
import com.hand.hdsp.quality.domain.entity.Problem;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.vo.ProblemVO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.api.dto.ProblemDTO;
import com.hand.hdsp.quality.domain.repository.ProblemRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;

import java.util.List;
import java.util.Optional;

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
                ProblemVO problemVO) {
        problemVO.setTenantId(tenantId);
        List<ProblemVO> problems = problemRepository.listForTree(problemVO);
        List<ProblemVO> list = TreeBuilder.buildTree(problems, null,
                ProblemVO::getProblemId, ProblemVO::getProblemParentId);
        return Results.success(list);
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
        ProblemDTO problemDTO = problemRepository.selectDTOByPrimaryKeyAndTenant(problemId);
        if(problemDTO != null){
            List<Problem> problems = problemRepository.selectByCondition(Condition.builder(Problem.class).andWhere(
                    Sqls.custom().andEqualTo(Problem.FIELD_PROBLEM_ID, problemDTO.getProblemParentId())
            ).build());
            if(CollectionUtils.isNotEmpty(problems)){
                problemDTO.setProblemParentName(problems.get(0).getProblemName());
            }
        }
        return Results.success(problemDTO);
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
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody ProblemDTO problemDTO) {
        problemDTO.setTenantId(tenantId);
        this.validObject(problemDTO);
        problemRepository.insertDTOSelective(problemDTO);
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
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody ProblemDTO problemDTO) {

        if(problemDTO.getProblemDesc() == null){
            problemRepository.updateDTOOptional(problemDTO,problemDTO.getProblemDesc());
        }
        problemRepository.updateDTOWhereTenant(problemDTO, tenantId);
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
        List<ProblemDTO> child = problemRepository.listForChild(problemDTO);
        if (CollectionUtils.isNotEmpty(child)){
            throw new CommonException(ErrorCode.PROBLEM_HAS_CHILD_PROBLEM);
        }
        problemRepository.deleteByPrimaryKey(problemDTO);
        return Results.success();
    }
}
