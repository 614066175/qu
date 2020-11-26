package com.hand.hdsp.quality.app.service.impl;

import java.util.List;
import java.util.Objects;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.entity.ApprovalHeader;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.ApprovalHeaderRepository;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.DataStandardVersionRepository;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.feign.AssetFeign;
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
    private static final String DATA_STANDARD = "数据标准";
    private final DataStandardRepository dataStandardRepository;

    private final DataStandardVersionRepository dataStandardVersionRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final DataStandardMapper dataStandardMapper;

    private final ApprovalHeaderRepository approvalHeaderRepository;

    private final AssetFeign assetFeign;

    public DataStandServiceImpl(DataStandardRepository dataStandardRepository, DataStandardVersionRepository dataStandardVersionRepository, StandardExtraRepository standardExtraRepository, DataStandardMapper dataStandardMapper, ApprovalHeaderRepository approvalHeaderRepository, AssetFeign assetFeign) {
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardVersionRepository = dataStandardVersionRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.approvalHeaderRepository = approvalHeaderRepository;
        this.assetFeign = assetFeign;
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
        List<StandardExtraDTO> standardExtraDTOList = dataStandardDTO.getStandardExtraDTOList();
        if (CollectionUtils.isNotEmpty(standardDTOList)) {
            standardExtraDTOList.forEach(s -> {
                StandardExtraDTO extraDTO = StandardExtraDTO.builder()
                        .standardId(dataStandardDTO.getStandardId())
                        .extraKey(s.getExtraKey())
                        .extraValue(s.getExtraValue())
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
        DataStandardDTO dataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(standardId);
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, standardId)
                        .andEqualTo(StandardExtra.FIELD_VERSION_NUMBER, dataStandardDTO.getVersionNumber())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, "DATA")
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
            dataStandardDTO.setStandardExtraDTOList(standardExtraDTOS);
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
            List<ApprovalHeaderDTO> approvalHeaderDTOS = approvalHeaderRepository.selectDTOByCondition(Condition.builder(ApprovalHeader.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(ApprovalHeader.FIELD_RESOURCE_NAME, dataStandardDTO.getStandardName())
                            .andEqualTo(ApprovalHeader.FIELD_ITEM_TYPE, DATA_STANDARD)
                            .andEqualTo(ApprovalHeader.FIELD_OPERATION, StandardConstant.OFFLINE_APPROVING)
                            .andEqualTo(ApprovalHeader.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                    .build());
            if(CollectionUtils.isNotEmpty(approvalHeaderDTOS)){
                approvalHeaderRepository.deleteDTO(approvalHeaderDTOS.get(0));
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
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(DataStandardDTO dataStandardDTO) {
        DataStandardDTO oldDataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(dataStandardDTO.getStandardId());
        if(Objects.isNull(oldDataStandardDTO)){
            throw new CommonException("hdsp.xsta.err.data_standard_not_exist");
        }
        oldDataStandardDTO.setStatus(dataStandardDTO.getStatus());
        if (StandardConstant.ONLINE_APPROVING.equals(dataStandardDTO.getStatus())){
           doApprove(oldDataStandardDTO);
        }
        if(StandardConstant.OFFLINE_APPROVING.equals(dataStandardDTO.getStatus())){
           doApprove(oldDataStandardDTO);
        }
        dataStandardRepository.updateByDTOPrimaryKey(oldDataStandardDTO);
    }


    /**
     * 实现申请动作
     * @param oldDataStandardDTO DataStandardDTO
     */
    private void doApprove(DataStandardDTO oldDataStandardDTO){
        //创建申请头
        ApprovalHeaderDTO headerDTO = ApprovalHeaderDTO.builder()
                .resourceName(oldDataStandardDTO.getStandardName())
                .resourceDesc(oldDataStandardDTO.getStandardDesc())
                .itemType(DATA_STANDARD)
                .operation(oldDataStandardDTO.getStatus())
                .applyId(oldDataStandardDTO.getChargeId())
                .tenantId(oldDataStandardDTO.getTenantId())
                .build();
        assetFeign.create(oldDataStandardDTO.getTenantId(),headerDTO);
        ApprovalHeaderDTO approvalHeaderDTO = assetFeign.getByUnique(oldDataStandardDTO.getTenantId(), headerDTO).getBody();
        if(Objects.isNull(approvalHeaderDTO)){
            throw new CommonException("hdsp.xsta.err.approval_header_not_exist");
        }
        //创建申请行
        ApprovalLineDTO approvalLineDTO = ApprovalLineDTO.builder()
                .approvalId(approvalHeaderDTO.getApprovalId())
                .operation(StandardConstant.APPROVING)
                .tenantId(approvalHeaderDTO.getTenantId())
                .build();
        assetFeign.create(approvalLineDTO.getTenantId(),approvalLineDTO);
    }

    @Override
    public Page<DataStandardDTO> list(PageRequest pageRequest, DataStandardDTO dataStandardDTO) {
        return PageHelper.doPageAndSort(pageRequest,()->dataStandardMapper.list(dataStandardDTO));
    }

    @Override
    public void update(DataStandardDTO dataStandardDTO) {
        DataStandardDTO oldDataStandardDTO = dataStandardRepository.selectDTOByPrimaryKey(dataStandardDTO.getStandardId());
        dataStandardDTO.setObjectVersionNumber(oldDataStandardDTO.getObjectVersionNumber());
        dataStandardRepository.updateByDTOPrimaryKey(dataStandardDTO);
        List<StandardExtraDTO> standardExtraDTOList = dataStandardDTO.getStandardExtraDTOList();
        if(CollectionUtils.isNotEmpty(standardExtraDTOList)){
            standardExtraDTOList.forEach(s->{
                //判断额外信息键是否存在
                List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository
                        .selectDTOByCondition(Condition.builder(StandardExtra.class)
                                .andWhere(Sqls.custom()
                                .andEqualTo(StandardExtra.FIELD_STANDARD_ID,dataStandardDTO.getStandardId())
                                .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE,"DATA")
                                .andEqualTo(StandardExtra.FIELD_VERSION_NUMBER,dataStandardDTO.getVersionNumber())
                                .andEqualTo(StandardExtra.FIELD_EXTRA_KEY,s.getExtraKey()))
                                .build());
                if(CollectionUtils.isNotEmpty(standardExtraDTOS)){
                    //更新值
                    StandardExtraDTO standardExtraDTO = standardExtraDTOS.get(0);
                    standardExtraDTO.setObjectVersionNumber(standardExtraDTO.getObjectVersionNumber()+1);
                    standardExtraDTO.setExtraValue(s.getExtraValue());
                    standardExtraRepository.updateByDTOPrimaryKey(standardExtraDTO);
                }else{
                    //新增
                    StandardExtraDTO standardExtraDTO = StandardExtraDTO.builder()
                            .standardId(dataStandardDTO.getStandardId())
                            .versionNumber(dataStandardDTO.getVersionNumber())
                            .standardType("DATA")
                            .extraKey(s.getExtraKey())
                            .extraValue(s.getExtraValue())
                            .tenantId(dataStandardDTO.getTenantId())
                            .build();
                    standardExtraRepository.insertDTOSelective(standardExtraDTO);
                }
            });
        }
    }
}
