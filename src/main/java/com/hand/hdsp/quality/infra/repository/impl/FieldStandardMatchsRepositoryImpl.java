package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatchs;
import com.hand.hdsp.quality.api.dto.FieldStandardMatchsDTO;
import com.hand.hdsp.quality.domain.repository.FieldStandardMatchsRepository;
import com.hand.hdsp.quality.infra.mapper.FieldStandardMatchsMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>字段标准匹配记录表资源库实现</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
@Component
public class FieldStandardMatchsRepositoryImpl extends BaseRepositoryImpl<FieldStandardMatchs, FieldStandardMatchsDTO> implements FieldStandardMatchsRepository {

    private final FieldStandardMatchsMapper fieldStandardMatchsMapper;

    public FieldStandardMatchsRepositoryImpl(FieldStandardMatchsMapper fieldStandardMatchsMapper) {
        this.fieldStandardMatchsMapper = fieldStandardMatchsMapper;
    }

    /**
     * @param pageRequest 分页参数
     * @param fieldStandardMatchsDTO 查询条件
     * @return 分页的字段标准匹配记录列表
     */
    @Override
    public Page<FieldStandardMatchsDTO> pageMatches(PageRequest pageRequest, FieldStandardMatchsDTO fieldStandardMatchsDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> fieldStandardMatchsMapper.listAll(fieldStandardMatchsDTO));
    }
}
