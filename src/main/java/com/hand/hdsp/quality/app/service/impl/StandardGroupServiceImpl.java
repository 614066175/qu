package com.hand.hdsp.quality.app.service.impl;

import java.util.List;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.app.service.StandardGroupService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.mapper.StandardGroupMapper;
import com.hand.hdsp.quality.infra.vo.StandardGroupVO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 21:08
 * @since 1.0
 */
@Service
public class StandardGroupServiceImpl implements StandardGroupService {

    private final StandardGroupRepository standardGroupRepository;

    private final StandardGroupMapper standardGroupMapper;

    private final DataStandardRepository dataStandardRepository;


    public StandardGroupServiceImpl(StandardGroupRepository standardGroupRepository, StandardGroupMapper standardGroupMapper, DataStandardRepository dataStandardRepository) {
        this.standardGroupRepository = standardGroupRepository;
        this.standardGroupMapper = standardGroupMapper;
        this.dataStandardRepository = dataStandardRepository;
    }

    @Override
    public List<StandardGroupDTO> listByGroup(StandardGroupVO standardGroupVO) {
        return standardGroupMapper.listByGroup(standardGroupVO);
    }

    @Override
    public void delete(StandardGroupDTO standardGroupDTO) {
        //判断目录是否包含子目录
        List<StandardGroupDTO> standardGroups = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom().andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, standardGroupDTO.getGroupId()))
                .build());
        if(CollectionUtils.isNotEmpty(standardGroups)){
            throw new CommonException("hdsp.xsta.err.group_has_child_group");
        }
        //判断是否包含标准
        List<DataStandardDTO> dataStandardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom().andEqualTo(DataStandard.FIELD_GROUP_ID, standardGroupDTO.getGroupId()))
                .build());
        if(CollectionUtils.isNotEmpty(dataStandardDTOS)){
            throw new CommonException("hdsp.xsta.err.group_has_standard");
        }
        standardGroupRepository.deleteDTO(standardGroupDTO);
    }
}
