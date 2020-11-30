package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.NameExecHisDetail;
import com.hand.hdsp.quality.infra.dataobject.NameExecHisDetailDO;
import com.hand.hdsp.quality.api.dto.NameExecHisDetailDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>命名标准执行历史详情表转换器</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameExecHisDetailConverter implements ConvertorI<NameExecHisDetail, NameExecHisDetailDO, NameExecHisDetailDTO> {

    @Override
    public NameExecHisDetail dtoToEntity( NameExecHisDetailDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    NameExecHisDetail entity = NameExecHisDetail.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public NameExecHisDetailDTO entityToDto(NameExecHisDetail entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    NameExecHisDetailDTO dto= NameExecHisDetailDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}