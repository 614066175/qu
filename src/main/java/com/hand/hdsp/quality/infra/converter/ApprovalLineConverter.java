package com.hand.hdsp.quality.infra.converter;

import java.util.Optional;

import com.hand.hdsp.quality.api.dto.ApprovalLineDTO;
import com.hand.hdsp.quality.domain.entity.ApprovalLine;
import com.hand.hdsp.quality.infra.dataobject.ApprovalLineDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * <p>申请行表转换器</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Component
public class ApprovalLineConverter implements ConvertorI<ApprovalLine, ApprovalLineDO, ApprovalLineDTO> {

    @Override
    public ApprovalLine dtoToEntity(ApprovalLineDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    ApprovalLine entity = ApprovalLine.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ApprovalLineDTO entityToDto(ApprovalLine entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ApprovalLineDTO dto= ApprovalLineDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}