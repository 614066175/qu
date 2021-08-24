package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.ProblemDTO;
import com.hand.hdsp.quality.domain.entity.Problem;
import com.hand.hdsp.quality.infra.vo.ProblemVO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>问题库表Mapper</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
public interface ProblemMapper extends BaseMapper<Problem> {

    /**
     * 查询问题库列表
     * @param problemVO 查询条件实体
     * @return 返回值
     */
    List<ProblemVO> list(ProblemVO problemVO);

    /**
     * 查询子节点
     * @param problemDTO 查询条件实体
     * @return 返回值
     */
    List<ProblemDTO> listForChild(ProblemDTO problemDTO);
}
