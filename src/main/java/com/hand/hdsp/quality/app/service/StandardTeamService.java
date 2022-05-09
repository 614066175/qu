package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>标准表应用服务</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
public interface StandardTeamService {

    /**
     * 列表分页查询
     * @param pageRequest
     * @param standardTeamDTO
     * @return
     */
    Page<StandardTeamDTO> list(PageRequest pageRequest, StandardTeamDTO standardTeamDTO);

    /**
     * @param standardTeamDTO
     * @return
     */
    List<StandardTeamDTO> listAll(StandardTeamDTO standardTeamDTO);

    /**
     * 删除标准组
     * @param standardTeamId
     */
    void remove(Long standardTeamId);
}
