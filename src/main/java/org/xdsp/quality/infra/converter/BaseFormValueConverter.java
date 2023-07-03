package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.BaseFormValueDTO;
import org.xdsp.quality.domain.entity.BaseFormValue;
import org.xdsp.quality.infra.dataobject.BaseFormValueDO;

import java.util.Optional;

/**
 * <p>质检项表单值转换器</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Component
public class BaseFormValueConverter implements ConvertorI<BaseFormValue, BaseFormValueDO, BaseFormValueDTO> {

    @Override
    public BaseFormValue dtoToEntity(BaseFormValueDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    BaseFormValue entity = BaseFormValue.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public BaseFormValueDTO entityToDto(BaseFormValue entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    BaseFormValueDTO dto = BaseFormValueDTO.builder().build();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}