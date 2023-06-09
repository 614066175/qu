package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.quality.api.dto.StandardAimDTO;
import org.xdsp.quality.infra.vo.ColumnVO;

import java.util.List;

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

    /**
     * @param standardAimDTOList
     */
    void batchDelete(List<StandardAimDTO> standardAimDTOList, Long tenantId, Long projectId);

    List<StandardAimDTO> reverseAim(Long tenantId, List<StandardAimDTO> standardAimDTOList);
}
