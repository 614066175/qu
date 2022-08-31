package com.hand.hdsp.quality.infra.converter;

import com.hand.hdsp.quality.domain.entity.StandardApproval;
import com.hand.hdsp.quality.infra.dataobject.StandardApprovalDO;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;

import io.choerodon.core.convertor.ConvertorI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;

/**
 * <p>各种标准审批表转换器</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@Component
public class StandardApprovalConverter implements ConvertorI<StandardApproval, StandardApprovalDO, StandardApprovalDTO> {

    @Override
    public StandardApproval dtoToEntity( StandardApprovalDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    StandardApproval entity = StandardApproval.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StandardApprovalDTO entityToDto(StandardApproval entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    StandardApprovalDTO dto= StandardApprovalDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}