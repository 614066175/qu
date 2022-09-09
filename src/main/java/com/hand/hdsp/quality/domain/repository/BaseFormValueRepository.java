package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BaseFormValueDTO;
import com.hand.hdsp.quality.domain.entity.BaseFormValue;

import java.util.List;

/**
 * <p>质检项表单值资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
public interface BaseFormValueRepository extends BaseRepository<BaseFormValue, BaseFormValueDTO>, ProxySelf<BaseFormValueRepository> {

    List<BaseFormValueDTO> selectByPlanBaseId(Long planBaseId);

    List<BaseFormValueDTO> selectFormItem();

}