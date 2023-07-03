package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.StandardTeamDTO;
import org.xdsp.quality.domain.entity.StandardTeam;

import java.util.List;

/**
 * <p>标准表Mapper</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
public interface StandardTeamMapper extends BaseMapper<StandardTeam> {

    /**
     * 查询所有标准组
     * @param standardTeamDTO
     * @return
     */
    List<StandardTeamDTO> listAll(StandardTeamDTO standardTeamDTO);

    /**
     * @param standardTeamId
     * @return
     */
    StandardTeamDTO detail(Long standardTeamId);
}
