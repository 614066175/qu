package org.xdsp.quality.infra.converter;

import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.ProblemDTO;
import org.xdsp.quality.domain.entity.Problem;
import org.xdsp.quality.infra.dataobject.ProblemDO;

import java.util.Optional;

/**
 * <p>问题库表转换器</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Component
public class ProblemConverter implements ConvertorI<Problem, ProblemDO, ProblemDTO> {

    @Override
    public Problem dtoToEntity( ProblemDTO dto) {
        return Optional.ofNullable(dto)
                .map(o -> {
                    Problem entity = Problem.builder().build();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                }).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public ProblemDTO entityToDto(Problem entity) {

        return Optional.ofNullable(entity)
                .map(o -> {
                    ProblemDTO dto= ProblemDTO.builder().build();
                    BeanUtils.copyProperties(entity,dto);
                    return dto;
                }).orElseThrow(IllegalArgumentException::new);
    }
}