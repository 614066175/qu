package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceDataValue;
import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;
import com.hand.hdsp.core.base.ProxySelf;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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