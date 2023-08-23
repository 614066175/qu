package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.NameExecHisDetailDTO;
import org.xdsp.quality.domain.entity.NameExecHisDetail;

import java.util.List;

/**
 * <p>命名标准执行历史详情表Mapper</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameExecHisDetailMapper extends BaseMapper<NameExecHisDetail> {

    /**
     * 查询执行详细信息
     *
     * @param historyId 执行历史ID
     * @return List<NameExecHisDetailDTO>
     */
    List<NameExecHisDetailDTO> list(Long historyId);
}
