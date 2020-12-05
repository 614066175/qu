package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.infra.vo.ColumnVO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>标准落标表应用服务</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
public interface StandardAimService {

    /**
     * 查找未落标的字段
     * @param standardAimDTO
     * @return
     */
    List<ColumnVO> unAimField(StandardAimDTO standardAimDTO);

    /**
     *
     * @param pageRequest
     * @param standardAimDTO
     * @return
     */
    Page<StandardAimDTO> list(PageRequest pageRequest, StandardAimDTO standardAimDTO);
}
