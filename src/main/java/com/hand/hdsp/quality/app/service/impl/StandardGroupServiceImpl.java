package com.hand.hdsp.quality.app.service.impl;

import java.util.List;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.app.service.StandardGroupService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardDoc;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.mapper.StandardGroupMapper;
import com.hand.hdsp.quality.infra.vo.StandardGroupVO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.hzero.export.vo.ExportParam;
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

    private final StandardDocRepository standardDocRepository;


    public StandardGroupServiceImpl(StandardGroupRepository standardGroupRepository, StandardGroupMapper standardGroupMapper, DataStandardRepository dataStandardRepository, StandardDocRepository standardDocRepository) {
        this.standardGroupRepository = standardGroupRepository;
        this.standardGroupMapper = standardGroupMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.standardDocRepository = standardDocRepository;
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
        if (CollectionUtils.isNotEmpty(standardGroups)) {
            throw new CommonException(ErrorCode.GROUP_HAS_CHILD_GROUP);
        }
        //判断是否包含标准
        List<DataStandardDTO> dataStandardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom().andEqualTo(DataStandard.FIELD_GROUP_ID, standardGroupDTO.getGroupId()))
                .build());
        if (CollectionUtils.isNotEmpty(dataStandardDTOS)) {
            throw new CommonException(ErrorCode.GROUP_HAS_STANDARD);
        }
        standardGroupRepository.deleteDTO(standardGroupDTO);
    }

    @Override
    public Page<T> list(PageRequest pageRequest, StandardGroupVO standardGroupVO) {
        //数据标准
        if ("DATA".equals(standardGroupVO.getStandardType())) {
            Condition condition = Condition.builder(DataStandard.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(DataStandard.FIELD_GROUP_ID, standardGroupVO.getGroupId()))
                    .build();
            return PageHelper.doPageAndSort(pageRequest, () -> dataStandardRepository.selectDTOByCondition(condition));
        }
        //字段标准
        //命名标准
        //标准文档
        return null;
    }

    @Override
    public List<StandardGroupDTO> export(StandardGroupDTO dto, ExportParam exportParam) {
        List<StandardGroupDTO> standardGroupDTOS = null;
        if ("DATA".equals(dto.getStandardType())) {
            //获取DATA所有的分组已经分组下的标准
            standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, "DATA"))
                    .build());
            if (CollectionUtils.isNotEmpty(standardGroupDTOS)) {
                standardGroupDTOS.forEach(standardGroupDTO -> {
                    List<DataStandardDTO> dataStandardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(DataStandard.FIELD_GROUP_ID, standardGroupDTO.getGroupId()))
                            .build());
                    standardGroupDTO.setDataStandardDTOList(dataStandardDTOList);
                    //是否排除没有内容的分组导出
                });
            }
        }
        if ("DOC".equals(dto.getStandardType())) {
            //获取DATA所有的分组已经分组下的标准
            standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, "DOC"))
                    .build());
            if (CollectionUtils.isNotEmpty(standardGroupDTOS)) {
                standardGroupDTOS.forEach(standardGroupDTO -> {
                    List<StandardDocDTO> standardDocDTOList = standardDocRepository.selectDTOByCondition(Condition.builder(StandardDoc.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(StandardDoc.FIELD_GROUP_ID, standardGroupDTO.getGroupId()))
                            .build());
                    standardGroupDTO.setStandardDocDTOList(standardDocDTOList);
                });
            }
        }
        return standardGroupDTOS;
    }
}
