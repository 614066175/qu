package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.ReferenceDataDTO;
import org.xdsp.quality.domain.entity.ReferenceData;

import java.util.List;

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
     * 列表查询 不分页
     * @param referenceDataDTO  查询条件
     * @return                  数据
     */
    List<ReferenceDataDTO> list(ReferenceDataDTO referenceDataDTO);

    /**
     * 根据主键查询详情
     * @param dataId        主键
     * @return              详细信息
     */
    ReferenceDataDTO detail(Long dataId);
}