package com.hand.hdsp.quality.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:40
 * @since 1.0
 */
@Service
public class DataStandServiceImpl implements DataStandardService {

    private static final Long DEFAULT_VERSION = 1L;
    private final DataStandardRepository dataStandardRepository;

    private final StandardExtraRepository standardExtraRepository;

    public DataStandServiceImpl(DataStandardRepository dataStandardRepository, StandardExtraRepository standardExtraRepository) {
        this.dataStandardRepository = dataStandardRepository;
        this.standardExtraRepository = standardExtraRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DataStandardDTO dataStandardDTO) {
        List<DataStandardDTO> dataStandardDTOS = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_STANDARD_CODE, dataStandardDTO.getStandardCode())
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(dataStandardDTOS)) {
            throw new CommonException("hdsp.xsta.err.data_standard_code_exist");
        }

        List<DataStandardDTO> standardDTOList = dataStandardRepository.selectDTOByCondition(Condition.builder(DataStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandard.FIELD_STANDARD_NAME, dataStandardDTO.getStandardName())
                        .andEqualTo(DataStandard.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardDTOList)) {
            throw new CommonException("hdsp.xsta.err.data_standard_name_exist");
        }

        dataStandardDTO.setVersionNumber(DEFAULT_VERSION);
        dataStandardDTO.setStatus(StandardConstant.CREATE);
        dataStandardRepository.insertDTOSelective(dataStandardDTO);

        if (!Objects.isNull(dataStandardDTO.getExtra())) {
            Map<String, String> extra = dataStandardDTO.getExtra();
            extra.forEach((k, v) -> {
                StandardExtraDTO extraDTO = StandardExtraDTO.builder()
                        .extraKey(k)
                        .extraValue(v)
                        .standardType("DATA")
                        .tenantId(dataStandardDTO.getTenantId())
                        .versionNumber(DEFAULT_VERSION)
                        .build();
                standardExtraRepository.insertDTOSelective(extraDTO);
            });
        }
    }

    @Override
    public DataStandardDTO detail(Long tenantId, Long standardId) {
        DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKeyAndTenant(standardId);
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, standardId)
                        .andEqualTo(StandardExtra.FIELD_VERSION_NUMBER, dataStandardDTO.getVersionNumber())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "DATA")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
            Map<String, String> extra = standardExtraDTOS.stream().collect(Collectors.toMap(StandardExtraDTO::getExtraKey, StandardExtraDTO::getExtraValue));
            dataStandardDTO.setExtra(extra);
        }
        return dataStandardDTO;
    }

    @Override
    public void delete(DataStandardDTO dataStandardDTO) {
        if(StandardConstant.ONLINE.equals(dataStandardDTO.getStatus())
                ||StandardConstant.OFFLINE_APPROVING.equals(dataStandardDTO.getStatus())){
            throw new CommonException("hdsp.xsta.err.data_standard_status_can_not_delete");
        }
        if(StandardConstant.ONLINE_APPROVING.equals(dataStandardDTO.getStatus())){

        }
        dataStandardRepository.deleteDTO(dataStandardDTO);
        if(!Objects.isNull(dataStandardDTO.getExtra())){

        }
    }

    @Override
    public void updateStatus(DataStandardDTO dataStandardDTO) {

    }
}
