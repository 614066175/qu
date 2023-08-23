package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.ReferenceDataHistoryDTO;
import org.xdsp.quality.domain.entity.ReferenceDataHistory;

/**
 * <p>参考数据头表资源库</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataHistoryRepository extends BaseRepository<ReferenceDataHistory, ReferenceDataHistoryDTO>, ProxySelf<ReferenceDataHistoryRepository> {

    /**
     * 查询最大版本
     * @param dataId    参考数据ID
     * @return          最大版本
     */
    Long queryMaxVersion(Long dataId);

    /**
     * list
     * @param dataId        参考数据ID
     * @param pageRequest   分页参数
     * @return              当前参考数据的历史版本
     */
    Page<ReferenceDataHistoryDTO> list(Long dataId, PageRequest pageRequest);

    /**
     * 根据主键查询详情
     * @param historyId     主键
     * @return              结果
     */
    ReferenceDataHistoryDTO detail(Long historyId);
}