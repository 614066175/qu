package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.ProblemDTO;
import com.hand.hdsp.quality.infra.vo.ProblemVO;

import java.util.List;

/**
 * <p>问题库表应用服务</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
public interface ProblemService {

    /**
     * 为树查询准备的list
     * @param problemVO 查询条件
     * @return 返回list
     */
    List<ProblemVO> listForTree(ProblemVO problemVO);

    /**
     * 查询问题详情
     * @param problemId 查询条件
     * @return 返回值
     */
    ProblemDTO detail(Long problemId);

    /**
     * 删除问题
     * @param problemDTO 删除条件
     */
    void deleteProblem(ProblemDTO problemDTO);

}
