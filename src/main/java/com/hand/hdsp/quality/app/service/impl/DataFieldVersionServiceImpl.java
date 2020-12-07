package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;
import com.hand.hdsp.quality.api.dto.ExtraVersionDTO;
import com.hand.hdsp.quality.app.service.DataFieldVersionService;
import com.hand.hdsp.quality.domain.entity.ExtraVersion;
import com.hand.hdsp.quality.domain.repository.ExtraVersionRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.mapper.DataFieldVersionMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * <p>字段标准版本表应用服务默认实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Service
public class DataFieldVersionServiceImpl implements DataFieldVersionService {

    private DataFieldVersionMapper dataFieldVersionMapper;

    private ExtraVersionRepository extraVersionRepository;

    public DataFieldVersionServiceImpl(DataFieldVersionMapper dataFieldVersionMapper, ExtraVersionRepository extraVersionRepository) {
        this.dataFieldVersionMapper = dataFieldVersionMapper;
        this.extraVersionRepository = extraVersionRepository;
    }

    @Override
    public Page<DataFieldVersionDTO> list(PageRequest pageRequest, DataFieldVersionDTO dataFieldVersionDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataFieldVersionMapper.list(dataFieldVersionDTO));
    }

    @Override
    public DataFieldVersionDTO detail(Long versionId) {
        DataFieldVersionDTO dataFieldVersionDTO = dataFieldVersionMapper.detail(versionId);
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
        return dataFieldVersionDTO;
    }
}
