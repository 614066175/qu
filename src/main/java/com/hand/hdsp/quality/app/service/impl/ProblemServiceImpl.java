package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.ProblemDTO;
import com.hand.hdsp.quality.app.service.ProblemService;
import com.hand.hdsp.quality.domain.entity.Problem;
import com.hand.hdsp.quality.domain.entity.Suggest;
import com.hand.hdsp.quality.domain.repository.ProblemRepository;
import com.hand.hdsp.quality.domain.repository.SuggestRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.vo.ProblemVO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.util.Results;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.persistence.RollbackException;

/**
 * <p>问题库表应用服务默认实现</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Service
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SuggestRepository suggestRepository;

    public ProblemServiceImpl(ProblemRepository problemRepository, SuggestRepository suggestRepository) {
        this.problemRepository = problemRepository;
        this.suggestRepository = suggestRepository;
    }

    @Override
    public List<ProblemVO> listForTree(ProblemVO problemVO) {
        List<ProblemVO> problems = problemRepository.listForTree(problemVO);
        return TreeBuilder.buildTree(problems, null,
                ProblemVO::getProblemId, ProblemVO::getProblemParentId);

    }

    @Override
    public ProblemDTO detail(Long problemId) {
        //前端需要
        if (problemId == -1) {
            return ProblemDTO.builder().build();
        }
        ProblemDTO problemDTO = problemRepository.selectDTOByPrimaryKeyAndTenant(problemId);
        if (problemDTO != null) {
            List<Problem> problems = problemRepository.selectByCondition(Condition.builder(Problem.class).andWhere(
                    Sqls.custom().andEqualTo(Problem.FIELD_PROBLEM_ID, problemDTO.getProblemParentId())
            ).build());
            if (CollectionUtils.isNotEmpty(problems)) {
                problemDTO.setProblemParentName(problems.get(0).getProblemName());
            }
        }
        return problemDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProblem(ProblemDTO problemDTO) {

        List<ProblemDTO> child = problemRepository.listForChild(problemDTO);
        if (CollectionUtils.isNotEmpty(child)) {
            throw new CommonException(ErrorCode.PROBLEM_HAS_CHILD_PROBLEM);
        }
        problemRepository.deleteByPrimaryKey(problemDTO);
        //级联删除建议
        List<Suggest> suggests = suggestRepository.selectByCondition(Condition.builder(Suggest.class)
                .andWhere(Sqls.custom().andEqualTo(Suggest.FIELD_PROBLEM_ID, problemDTO.getProblemId())).build());
        suggestRepository.batchDeleteByPrimaryKey(suggests);
    }

    @Override
    public int create(ProblemDTO dto) {
        if (dto.getProblemParentId() == null) {
            dto.setProblemParentId(0L);
        }
        // 校验父目录下是否有标准
//        if (ruleGroupDTO.getParentGroupId() != null) {
//            RuleDTO dto = ruleGroupRepository.selectDTOByPrimaryKey(standardGroupDTO.getParentGroupId());
//            existStandard(dto);
//        }
        // 校验编码存在
        List<ProblemDTO> dtoList = problemRepository.selectDTOByCondition(Condition.builder(Problem.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Problem.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(Problem.FIELD_PROBLEM_CODE, dto.getProblemCode()))
                .build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        // 校验名称存在
        dtoList = problemRepository.selectDTOByCondition(Condition.builder(Problem.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Problem.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(Problem.FIELD_PROBLEM_NAME, dto.getProblemName())
                        .andEqualTo(Problem.FIELD_PROBLEM_PARENT_ID, dto.getProblemParentId())).build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.GROUP_NAME_ALREADY_EXIST);
        }
        return problemRepository.insertDTOSelective(dto);
    }
}
