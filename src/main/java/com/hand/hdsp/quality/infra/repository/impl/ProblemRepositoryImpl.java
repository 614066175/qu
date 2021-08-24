package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.Problem;
import com.hand.hdsp.quality.api.dto.ProblemDTO;
import com.hand.hdsp.quality.domain.repository.ProblemRepository;
import com.hand.hdsp.quality.infra.mapper.ProblemMapper;
import com.hand.hdsp.quality.infra.vo.ProblemVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>问题库表资源库实现</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Component
public class ProblemRepositoryImpl extends BaseRepositoryImpl<Problem, ProblemDTO> implements ProblemRepository {

    private final ProblemMapper problemMapper;

    public ProblemRepositoryImpl(ProblemMapper problemMapper) {
        this.problemMapper = problemMapper;
    }

    @Override
    public List<ProblemVO> listForTree(ProblemVO problemVO) {
        return problemMapper.list(problemVO);
    }

    @Override
    public List<ProblemDTO> listForChild(ProblemDTO problemDTO) {
        return problemMapper.listForChild(problemDTO);
    }
}
