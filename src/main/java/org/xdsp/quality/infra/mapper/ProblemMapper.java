package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.ProblemDTO;
import org.xdsp.quality.domain.entity.Problem;
import org.xdsp.quality.infra.vo.ProblemVO;

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
