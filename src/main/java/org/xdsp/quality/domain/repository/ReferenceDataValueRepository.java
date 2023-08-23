package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.ReferenceDataValueDTO;
import org.xdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import org.xdsp.quality.domain.entity.ReferenceDataValue;

import java.util.List;

/**
 * <p>参考数据值资源库</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataValueRepository extends BaseRepository<ReferenceDataValue, ReferenceDataValueDTO>, ProxySelf<ReferenceDataValueRepository> {

    /**
     * 列表查询
     * @param referenceDataValueDTO 查询参数
     * @param pageRequest           分页参数
     * @return                      结果
     */
    Page<ReferenceDataValueDTO> list(ReferenceDataValueDTO referenceDataValueDTO, PageRequest pageRequest);

    /**
     * 列表查询 不分页
     * @param referenceDataValueDTO 查询条件
     * @return                      结果
     */
    List<ReferenceDataValueDTO> list(ReferenceDataValueDTO referenceDataValueDTO);

    /**
     * 列表查询
     * @param dataId 参考数据ID
     * @return       结果
     */
    List<SimpleReferenceDataValueDTO> simpleQueryByDataId(Long dataId);
}