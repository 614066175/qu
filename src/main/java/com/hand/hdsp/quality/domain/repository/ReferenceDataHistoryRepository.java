package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.ReferenceDataHistory;
import com.hand.hdsp.quality.api.dto.ReferenceDataHistoryDTO;
import com.hand.hdsp.core.base.ProxySelf;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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