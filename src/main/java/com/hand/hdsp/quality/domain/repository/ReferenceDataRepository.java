package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.ReferenceData;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.core.base.ProxySelf;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>参考数据头表资源库</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataRepository extends BaseRepository<ReferenceData, ReferenceDataDTO>, ProxySelf<ReferenceDataRepository> {

    /**
     * 列表查询
     * @param referenceDataDTO  查询参数
     * @param pageRequest       分页参数
     * @return                  数据
     */
    Page<ReferenceDataDTO> list(ReferenceDataDTO referenceDataDTO, PageRequest pageRequest);

    /**
     * 根据主键查询详情
     * @param dataId        主键
     * @return              详细信息
     */
    ReferenceDataDTO detail(Long dataId);
}