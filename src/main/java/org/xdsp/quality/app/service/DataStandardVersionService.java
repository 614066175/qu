package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.quality.api.dto.DataStandardVersionDTO;

/**
 * <p>数据标准版本表应用服务</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface DataStandardVersionService {

    /**
     * 数据标准历史表数据
     * @param versionId
     * @return
     */
    DataStandardVersionDTO detail(Long versionId);

    /**
     *
     *
     * @param pageRequest
     * @param dataStandardVersionDTO
     * @return
     */
    Page<DataStandardVersionDTO> list(PageRequest pageRequest, DataStandardVersionDTO dataStandardVersionDTO);
}
