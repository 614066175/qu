package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.ItemTemplateSqlDTO;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.infra.dataobject.ItemTemplateSqlDO;

import java.util.Optional;

/**
 * <p>校验项模板SQL表转换器</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
@Component
public class ItemTemplateSqlConverter implements ConvertorI<ItemTemplateSql, ItemTemplateSqlDO, ItemTemplateSqlDTO> {

    @Override
    public ItemTemplateSql dtoToEntity( ItemTemplateSqlDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ItemTemplateSql entity = ItemTemplateSql.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ItemTemplateSqlDTO entityToDto(ItemTemplateSql entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ItemTemplateSqlDTO dto= ItemTemplateSqlDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}
