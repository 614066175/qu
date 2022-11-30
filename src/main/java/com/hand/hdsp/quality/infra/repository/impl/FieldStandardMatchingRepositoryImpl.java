package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatching;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;
import com.hand.hdsp.quality.domain.repository.FieldStandardMatchingRepository;
import com.hand.hdsp.quality.infra.mapper.FieldStandardMatchingMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>字段标准匹配表资源库实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@Component
public class FieldStandardMatchingRepositoryImpl extends BaseRepositoryImpl<FieldStandardMatching, FieldStandardMatchingDTO> implements FieldStandardMatchingRepository {

    private final FieldStandardMatchingMapper fieldStandardMatchingMapper;

    public FieldStandardMatchingRepositoryImpl(FieldStandardMatchingMapper fieldStandardMatchingMapper) {
        this.fieldStandardMatchingMapper = fieldStandardMatchingMapper;
    }

    /**
     * @param pageRequest 分页条件
     * @param fieldStandardMatchingDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    @Override
    public Page<FieldStandardMatchingDTO> pageFieldStandardMatching(PageRequest pageRequest, FieldStandardMatchingDTO fieldStandardMatchingDTO) {
        return PageHelper.doPageAndSort(pageRequest,() -> fieldStandardMatchingMapper.findAllMatchings(fieldStandardMatchingDTO));
    }
}
