package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.xdsp.quality.api.dto.DataStandardVersionDTO;
import org.xdsp.quality.api.dto.ExtraVersionDTO;
import org.xdsp.quality.app.service.DataStandardVersionService;
import org.xdsp.quality.domain.entity.ExtraVersion;
import org.xdsp.quality.domain.repository.ExtraVersionRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.mapper.DataStandardVersionMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>数据标准版本表应用服务默认实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Service
public class DataStandardVersionServiceImpl implements DataStandardVersionService {

    private DataStandardVersionMapper dataStandardVersionMapper;

    private ExtraVersionRepository extraVersionRepository;

    public DataStandardVersionServiceImpl(DataStandardVersionMapper dataStandardVersionMapper, ExtraVersionRepository extraVersionRepository) {
        this.dataStandardVersionMapper = dataStandardVersionMapper;
        this.extraVersionRepository = extraVersionRepository;
    }

    @Override
    public DataStandardVersionDTO detail(Long versionId) {
        DataStandardVersionDTO dataStandardVersionDTO = dataStandardVersionMapper.detail(versionId);
        if(DataSecurityHelper.isTenantOpen()){
            if(StringUtils.isNotEmpty(dataStandardVersionDTO.getChargeName())) {
                dataStandardVersionDTO.setChargeName(DataSecurityHelper.decrypt(dataStandardVersionDTO.getChargeName()));
            }
            if(StringUtils.isNotEmpty(dataStandardVersionDTO.getChargeName())) {
                dataStandardVersionDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataStandardVersionDTO.getChargeDeptName()));
            }
        }
        if (Objects.isNull(dataStandardVersionDTO)) {
            throw new CommonException(ErrorCode.DATA_STANDARD_VERSION_NOT_EXIST);
        }
        convertToDataLengthList(dataStandardVersionDTO);
        List<ExtraVersionDTO> extraVersionDTOS = extraVersionRepository.selectDTOByCondition(Condition.builder(ExtraVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_ID, dataStandardVersionDTO.getStandardId())
                        .andEqualTo(ExtraVersion.FIELD_STANDARD_TYPE, "DATA")
                        .andEqualTo(ExtraVersion.FIELD_VERSION_NUMBER, dataStandardVersionDTO.getVersionNumber())
                        .andEqualTo(ExtraVersion.FIELD_TENANT_ID, dataStandardVersionDTO.getTenantId()))
                .build());
        dataStandardVersionDTO.setExtraVersionDTOList(extraVersionDTOS);
        return dataStandardVersionDTO;
    }

    @Override
    public Page<DataStandardVersionDTO> list(PageRequest pageRequest, DataStandardVersionDTO dataStandardVersionDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataStandardVersionMapper.list(dataStandardVersionDTO));
    }

    private void convertToDataLengthList(DataStandardVersionDTO dataStandardVersionDTO) {
        //对数据长度进行处理
        if (StringUtils.isNotEmpty(dataStandardVersionDTO.getDataLength()) ) {
            List<String> dataLength = Arrays.asList(dataStandardVersionDTO.getDataLength().split(","));
            List<Long> dataLengthList = dataLength.stream().map(Long::parseLong).collect(Collectors.toList());
            dataStandardVersionDTO.setDataLengthList(dataLengthList);
        }
    }
}
