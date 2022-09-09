package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BaseFormValueDTO;
import com.hand.hdsp.quality.domain.entity.BaseFormValue;
import com.hand.hdsp.quality.domain.repository.BaseFormValueRepository;
import com.hand.hdsp.quality.infra.mapper.BaseFormValueMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>质检项表单值资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Component
public class BaseFormValueRepositoryImpl extends BaseRepositoryImpl<BaseFormValue, BaseFormValueDTO> implements BaseFormValueRepository {

    private final BaseFormValueMapper baseFormValueMapper;

    public BaseFormValueRepositoryImpl(BaseFormValueMapper baseFormValueMapper) {
        this.baseFormValueMapper = baseFormValueMapper;
    }

    @Override
    public List<BaseFormValueDTO> selectByPlanBaseId(Long planBaseId) {
        return baseFormValueMapper.selectByPlanBaseId(planBaseId);
    }

    @Override
    public List<BaseFormValueDTO> selectFormItem() {
        return baseFormValueMapper.selectFormItem();
    }
}
