package com.hand.hdsp.quality.app.service.impl;


import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import java.util.List;
import java.util.Objects;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import com.hand.hdsp.quality.domain.entity.StandardApprove;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final DataStandardService dataStandardService;

    private final StandardAimRepository standardAimRepository;

    private final ExtraVersionRepository extraVersionRepository;


    public DataFieldServiceImpl(DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository, StandardApproveRepository standardApproveRepository, DataFieldVersionRepository dataFieldVersionRepository, DataFieldMapper dataFieldMapper, DataStandardService dataStandardService, StandardAimRepository standardAimRepository, ExtraVersionRepository extraVersionRepository) {
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.standardApproveRepository = standardApproveRepository;
        this.dataFieldVersionRepository = dataFieldVersionRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardService = dataStandardService;
        this.standardAimRepository = standardAimRepository;
        this.extraVersionRepository = extraVersionRepository;
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
            throw new CommonException(ErrorCode.DATA_FIELD_NAME_EXIST);
        }

        dataFieldDTO.setStandardStatus(CREATE);
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
        if (ONLINE.equals(dataFieldDTO.getStandardStatus())
                || OFFLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
            throw new CommonException(ErrorCode.DATA_FIELD_CAN_NOT_DELETE);
        }
        if (ONLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
            //删除申请记录表记录
            List<StandardApproveDTO> standardApproveDTOS = standardApproveRepository.selectDTOByCondition(Condition.builder(StandardApprove.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardApprove.FIELD_STANDARD_NAME, dataFieldDTO.getFieldName())
                            .andEqualTo(StandardApprove.FIELD_STANDARD_TYPE, "FIELD")
                            .andEqualTo(StandardApprove.FIELD_OPERATION, OFFLINE_APPROVING)
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

    @Override
    public void updateStatus(DataFieldDTO dataFieldDTO) {
        DataFieldDTO oldDataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
        if (Objects.isNull(oldDataFieldDTO)) {
            throw new CommonException(ErrorCode.DATA_FIELD_NAME_EXIST);
        }
        oldDataFieldDTO.setStandardStatus(dataFieldDTO.getStandardStatus());
        if (ONLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
            doApprove(oldDataFieldDTO);
        }
        if (OFFLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
            doApprove(oldDataFieldDTO);
        }
        dataFieldRepository.updateByDTOPrimaryKey(oldDataFieldDTO);
    }

    @Override
    public void aim(List<StandardAimDTO> standardAimDTOList) {
        dataStandardService.aim(standardAimDTOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishOrOff(DataFieldDTO dataFieldDTO) {
        DataFieldDTO dto = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
        if (Objects.isNull(dto)) {
            throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
        }
        dataFieldDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
        dataFieldRepository.updateDTOOptional(dataFieldDTO, DataField.FIELD_STANDARD_STATUS);
        if(ONLINE.equals(dataFieldDTO.getStandardStatus())){
            //存版本表
            doVersion(dataFieldDTO);
        }
    }

    @Override
    public List<DataFieldDTO> export(DataFieldDTO dto, ExportParam exportParam, PageRequest pageRequest) {
        return list(pageRequest, dto);
    }

    private void doVersion(DataFieldDTO dataFieldDTO) {
        Long lastVersion = 1L;
        List<DataFieldVersionDTO> dataFieldVersionDTOS = dataFieldVersionRepository.selectDTOByCondition(Condition.builder(DataFieldVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_ID, dataFieldDTO.getFieldId()))
                .orderByDesc(DataFieldVersion.FIELD_VERSION_NUMBER).build());
        DataFieldVersionDTO dataFieldVersionDTO = new DataFieldVersionDTO();
        //不为空则取最新版本
        if (CollectionUtils.isNotEmpty(dataFieldVersionDTOS)) {
            lastVersion = dataFieldVersionDTOS.get(0).getVersionNumber() + 1;
        }
        //存入版本表
        BeanUtils.copyProperties(dataFieldDTO, dataFieldVersionDTO);
        dataFieldVersionDTO.setVersionNumber(lastVersion);
        dataFieldVersionRepository.insertDTOSelective(dataFieldVersionDTO);
        //存附加信息版本表
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "FIELD")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
            //存附件信息版本表
            for (StandardExtraDTO s : standardExtraDTOS) {
                ExtraVersionDTO extraVersionDTO = new ExtraVersionDTO();
                BeanUtils.copyProperties(s, extraVersionDTO);
                extraVersionDTO.setVersionNumber(lastVersion);
                extraVersionRepository.insertDTOSelective(extraVersionDTO);
            }
        }
    }

    private void doApprove(DataFieldDTO oldDataFieldDTO) {
    }
}
