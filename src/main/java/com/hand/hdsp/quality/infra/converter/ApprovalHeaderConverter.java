package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.ApprovalHeaderDTO;
import com.hand.hdsp.quality.domain.entity.ApprovalHeader;
import com.hand.hdsp.quality.infra.dataobject.ApprovalHeaderDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>申请头表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Component
public class ApprovalHeaderConverter implements ConvertorI<ApprovalHeader, ApprovalHeaderDO, ApprovalHeaderDTO> {

    @Override
    public ApprovalHeader dtoToEntity(ApprovalHeaderDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ApprovalHeader entity = ApprovalHeader.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ApprovalHeaderDTO entityToDto(ApprovalHeader entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ApprovalHeaderDTO dto= ApprovalHeaderDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}