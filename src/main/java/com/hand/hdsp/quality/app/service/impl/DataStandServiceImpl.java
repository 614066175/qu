package com.hand.hdsp.quality.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.api.dto.StandardApproveDTO;
import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;
import com.hand.hdsp.quality.domain.entity.StandardApprove;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.DataStandardVersionRepository;
import com.hand.hdsp.quality.domain.repository.StandardApproveRepository;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
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

    private final DataStandardVersionRepository dataStandardVersionRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final DataStandardMapper dataStandardMapper;

    private final StandardApproveRepository standardApproveRepository;

    public DataStandServiceImpl(DataStandardRepository dataStandardRepository, DataStandardVersionRepository dataStandardVersionRepository, StandardExtraRepository standardExtraRepository, DataStandardMapper dataStandardMapper, StandardApproveRepository standardApproveRepository) {
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardVersionRepository = dataStandardVersionRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.standardApproveRepository = standardApproveRepository;
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
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataStandardDTO dataStandardDTO) {
        if(StandardConstant.ONLINE.equals(dataStandardDTO.getStatus())
                ||StandardConstant.OFFLINE_APPROVING.equals(dataStandardDTO.getStatus())){
            throw new CommonException("hdsp.xsta.err.data_standard_status_can_not_delete");
        }
        if(StandardConstant.ONLINE_APPROVING.equals(dataStandardDTO.getStatus())){
            //删除申请记录表记录
            List<StandardApproveDTO> standardApproveDTOS = standardApproveRepository.selectDTOByCondition(Condition.builder(StandardApprove.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardApprove.FIELD_STANDARD_NAME, dataStandardDTO.getStandardName())
                            .andEqualTo(StandardApprove.FIELD_STANDARD_TYPE, "DATA")
                            .andEqualTo(StandardApprove.FIELD_OPERATION, StandardConstant.OFFLINE_APPROVING)
                            .andEqualTo(StandardApprove.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                    .build());
            if(CollectionUtils.isNotEmpty(standardApproveDTOS)){
                standardApproveRepository.deleteDTO(standardApproveDTOS.get(0));
            }
        }
        dataStandardRepository.deleteDTO(dataStandardDTO);
        //删除版本表数据
        List<DataStandardVersionDTO> dataStandardVersionDTOS = dataStandardVersionRepository.selectDTOByCondition(Condition.builder(DataStandardVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataStandardVersion.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(DataStandardVersion.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        dataStandardVersionRepository.batchDTODelete(dataStandardVersionDTOS);
        //删除额外信息表数据
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataStandardDTO.getStandardId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "DATA")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        standardExtraRepository.batchDTODelete(standardExtraDTOS);
    }

    @Override
    public void updateStatus(DataStandardDTO dataStandardDTO) {

    }

    @Override
    public Page<DataStandardDTO> list(PageRequest pageRequest, DataStandardDTO dataStandardDTO) {
        return PageHelper.doPageAndSort(pageRequest,()->dataStandardMapper.list(dataStandardDTO));
    }
}
