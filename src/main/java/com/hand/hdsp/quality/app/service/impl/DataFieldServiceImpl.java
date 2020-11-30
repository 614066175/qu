package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;
import com.hand.hdsp.quality.api.dto.StandardApproveDTO;
import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import com.hand.hdsp.quality.domain.entity.StandardApprove;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.DataFieldVersionRepository;
import com.hand.hdsp.quality.domain.repository.StandardApproveRepository;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>字段标准表应用服务默认实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Service
public class DataFieldServiceImpl implements DataFieldService {

    private final DataFieldRepository dataFieldRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final StandardApproveRepository standardApproveRepository;

    private final DataFieldVersionRepository dataFieldVersionRepository;

    private final DataFieldMapper dataFieldMapper;


    public DataFieldServiceImpl(DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository, StandardApproveRepository standardApproveRepository, DataFieldVersionRepository dataFieldVersionRepository, DataFieldMapper dataFieldMapper) {
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.standardApproveRepository = standardApproveRepository;
        this.dataFieldVersionRepository = dataFieldVersionRepository;
        this.dataFieldMapper = dataFieldMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DataFieldDTO dataFieldDTO) {
        List<DataFieldDTO> dataFieldDTOS = dataFieldRepository.selectDTOByCondition(Condition.builder(DataField.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                        .andEqualTo(DataField.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(dataFieldDTOS)) {
            throw new CommonException("hdsp.xsta.err.data_field_name_already_exist");
        }

        dataFieldDTO.setStatus(StandardConstant.CREATE);
        dataFieldRepository.insertDTOSelective(dataFieldDTO);

        val standardExtraDTOList = dataFieldDTO.getStandardExtraDTOList();
        if (CollectionUtils.isNotEmpty(dataFieldDTOS)) {
            standardExtraDTOList.forEach(s -> {
                StandardExtraDTO extraDTO = StandardExtraDTO.builder()
                        .standardId(dataFieldDTO.getFieldId())
                        .extraKey(s.getExtraKey())
                        .extraValue(s.getExtraValue())
                        .standardType("FIELD")
                        .tenantId(dataFieldDTO.getTenantId())
                        .build();
                standardExtraRepository.insertDTOSelective(extraDTO);
            });
        }
    }

    @Override
    public DataFieldDTO detail(Long tenantId, Long fieldId) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(fieldId);
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, fieldId)
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "FIELD")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
            dataFieldDTO.setStandardExtraDTOList(standardExtraDTOS);
        }
        return dataFieldDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataFieldDTO dataFieldDTO) {
        if (StandardConstant.ONLINE.equals(dataFieldDTO.getStatus())
                || StandardConstant.OFFLINE_APPROVING.equals(dataFieldDTO.getStatus())) {
            throw new CommonException("hdsp.xsta.err.data_field_status_can_not_delete");
        }
        if (StandardConstant.ONLINE_APPROVING.equals(dataFieldDTO.getStatus())) {
            //删除申请记录表记录
            List<StandardApproveDTO> standardApproveDTOS = standardApproveRepository.selectDTOByCondition(Condition.builder(StandardApprove.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardApprove.FIELD_STANDARD_NAME, dataFieldDTO.getFieldName())
                            .andEqualTo(StandardApprove.FIELD_STANDARD_TYPE, "FIELD")
                            .andEqualTo(StandardApprove.FIELD_OPERATION, StandardConstant.OFFLINE_APPROVING)
                            .andEqualTo(StandardApprove.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                    .build());
            if (CollectionUtils.isNotEmpty(standardApproveDTOS)) {
                standardApproveRepository.deleteDTO(standardApproveDTOS.get(0));
            }
        }
        dataFieldRepository.deleteDTO(dataFieldDTO);
        //删除版本表数据
        List<DataFieldVersionDTO> dataStandardVersionDTOS = dataFieldVersionRepository.selectDTOByCondition(Condition.builder(DataFieldVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataFieldVersion.FIELD_FIELD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(DataFieldVersion.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                .build());
        dataFieldVersionRepository.batchDTODelete(dataStandardVersionDTOS);
        // 删除额外信息
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "FIELD")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                ).build());
        standardExtraRepository.batchDTODelete(standardExtraDTOS);
    }

    @Override
    public Page<DataFieldDTO> list(PageRequest pageRequest, DataFieldDTO dataFieldDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataFieldMapper.list(dataFieldDTO));
    }
}
