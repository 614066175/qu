package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.xdsp.quality.api.dto.DataFieldVersionDTO;
import org.xdsp.quality.api.dto.ExtraVersionDTO;
import org.xdsp.quality.api.dto.StandardTeamDTO;
import org.xdsp.quality.app.service.DataFieldVersionService;
import org.xdsp.quality.domain.entity.ExtraVersion;
import org.xdsp.quality.domain.entity.StandardRelation;
import org.xdsp.quality.domain.repository.ExtraVersionRepository;
import org.xdsp.quality.domain.repository.StandardRelationRepository;
import org.xdsp.quality.domain.repository.StandardTeamRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.mapper.DataFieldVersionMapper;
import org.xdsp.quality.infra.util.DataTranslateUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>字段标准版本表应用服务默认实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Service
public class DataFieldVersionServiceImpl implements DataFieldVersionService {

    private final DataFieldVersionMapper dataFieldVersionMapper;

    private final ExtraVersionRepository extraVersionRepository;

    private final StandardRelationRepository standardRelationRepository;

    private final StandardTeamRepository standardTeamRepository;

    private final DataTranslateUtil dataTranslateUtil;

    public DataFieldVersionServiceImpl(DataFieldVersionMapper dataFieldVersionMapper, ExtraVersionRepository extraVersionRepository, StandardRelationRepository standardRelationRepository, StandardTeamRepository standardTeamRepository, DataTranslateUtil dataTranslateUtil) {
        this.dataFieldVersionMapper = dataFieldVersionMapper;
        this.extraVersionRepository = extraVersionRepository;
        this.standardRelationRepository = standardRelationRepository;
        this.standardTeamRepository = standardTeamRepository;
        this.dataTranslateUtil = dataTranslateUtil;
    }

    @Override
    public Page<DataFieldVersionDTO> list(PageRequest pageRequest, DataFieldVersionDTO dataFieldVersionDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataFieldVersionMapper.list(dataFieldVersionDTO));
    }

    @Override
    public DataFieldVersionDTO detail(Long versionId) {
        DataFieldVersionDTO dataFieldVersionDTO = dataFieldVersionMapper.detail(versionId);
        if(DataSecurityHelper.isTenantOpen()){
            if(StringUtils.isNotEmpty(dataFieldVersionDTO.getChargeName())) {
                dataFieldVersionDTO.setChargeName(DataSecurityHelper.decrypt(dataFieldVersionDTO.getChargeName()));
            }
            if(StringUtils.isNotEmpty(dataFieldVersionDTO.getChargeName())) {
                dataFieldVersionDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldVersionDTO.getChargeDeptName()));
            }
        }
        if (Objects.isNull(dataFieldVersionDTO)) {
            throw new CommonException(ErrorCode.DATA_FIELD_VERSION_NOT_EXIST);
        }
//        convertToDataLengthList(dataStandardVersionDTO);
        List<ExtraVersionDTO> extraVersionDTOS = extraVersionRepository.selectDTOByCondition(Condition.builder(ExtraVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_ID, dataFieldVersionDTO.getFieldId())
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_TYPE, "FIELD")
                        .andEqualTo(ExtraVersion.FIELD_VERSION_NUMBER, dataFieldVersionDTO.getVersionNumber())
                        .andEqualTo(ExtraVersion.FIELD_TENANT_ID, dataFieldVersionDTO.getTenantId()))
                .build());
        dataFieldVersionDTO.setExtraVersionDTOList(extraVersionDTOS);

        //查询标准组
        List<StandardRelation> standardRelations = standardRelationRepository.select(StandardRelation.builder().fieldStandardId(dataFieldVersionDTO.getFieldId()).build());
        List<Long> standardTeamIds = standardRelations.stream()
                .map(StandardRelation::getStandardTeamId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(standardTeamIds)) {
            List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByIds(standardTeamIds);
            dataFieldVersionDTO.setStandardTeamDTOList(standardTeamDTOS);
        }

        // 翻译值域范围：翻译失败，返回原值
        String valueType = dataFieldVersionDTO.getValueType();
        String valueRange = dataFieldVersionDTO.getValueRange();
        Long tenantId = dataFieldVersionDTO.getTenantId();
        dataFieldVersionDTO.setValueRange(dataTranslateUtil.translateValueRange(valueType,valueRange,tenantId));

        return dataFieldVersionDTO;
    }
}
