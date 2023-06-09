package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BaseFormValueDTO;
import org.xdsp.quality.domain.entity.BaseFormValue;

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