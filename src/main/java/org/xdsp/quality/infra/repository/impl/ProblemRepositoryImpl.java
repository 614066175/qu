package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.ProblemDTO;
import org.xdsp.quality.domain.entity.Problem;
import org.xdsp.quality.domain.repository.ProblemRepository;
import org.xdsp.quality.infra.mapper.ProblemMapper;
import org.xdsp.quality.infra.vo.ProblemVO;

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
