package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.ProblemDTO;
import org.xdsp.quality.domain.entity.Problem;
import org.xdsp.quality.infra.vo.ProblemVO;

import java.util.List;

/**
 * <p>问题库表资源库</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
public interface ProblemRepository extends BaseRepository<Problem, ProblemDTO>, ProxySelf<ProblemRepository> {

    /**
     * 为树查询准备的list
     * @param problemVO 查询条件
     * @return 返回list
     */
    List<ProblemVO> listForTree(ProblemVO problemVO);

    /**
     * 查询该节点下是否有子节点或问题
     * @param problemDTO 查询条件
     * @return 返回list
     */
    List<ProblemDTO> listForChild(ProblemDTO problemDTO);
}