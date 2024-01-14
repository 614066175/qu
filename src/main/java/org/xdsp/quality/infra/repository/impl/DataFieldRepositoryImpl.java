package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.DataField;
import org.xdsp.quality.domain.repository.DataFieldRepository;
import org.xdsp.quality.domain.repository.StandardGroupRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.mapper.DataFieldMapper;
import org.xdsp.quality.infra.mapper.DataStandardMapper;
import org.xdsp.quality.infra.util.ImportUtil;

import java.util.ArrayList;
import java.util.List;

import static org.xdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.CREATE;

/**
 * <p>字段标准表资源库实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Component
public class DataFieldRepositoryImpl extends BaseRepositoryImpl<DataField, DataFieldDTO> implements DataFieldRepository {

    private final StandardGroupRepository standardGroupRepository;

    private final DataFieldMapper dataFieldMapper;

    private final DataStandardMapper dataStandardMapper;

    private final ImportUtil importUtil;

    public DataFieldRepositoryImpl(StandardGroupRepository standardGroupRepository,
                                   DataFieldMapper dataFieldMapper,
                                   DataStandardMapper dataStandardMapper, ImportUtil importUtil) {
        this.standardGroupRepository = standardGroupRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardMapper = dataStandardMapper;
        this.importUtil = importUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchImport(List<DataFieldDTO> dataFieldDTOList) {
        List<DataFieldDTO> importDataFieldDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            for (DataFieldDTO dataFieldDTO : dataFieldDTOList) {
                Condition condition = Condition.builder(DataField.class).andWhere(
                        Sqls.custom()
                                .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                                .andEqualTo(DataField.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                ).build();
                List<DataField> dataFields = selectByCondition(condition);
                if (CollectionUtils.isNotEmpty(dataFields)) {
                    throw new CommonException(ErrorCode.DATA_FIELD_NAME_EXIST);
                }

                //使用工具类获取责任人Id，和责任部门Id
                dataFieldDTO.setChargeDeptId(importUtil.getChargeDeptId(dataFieldDTO.getChargeDeptName(), dataFieldDTO.getTenantId()));
                dataFieldDTO.setChargeId(importUtil.getChargerId(dataFieldDTO.getChargeName(), dataFieldDTO.getTenantId()));

                StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                            .groupCode(dataFieldDTO.getGroupCode())
                            .groupName(dataFieldDTO.getGroupName())
                            .groupDesc(dataFieldDTO.getStandardDesc())
                            .standardType(FIELD)
                            .tenantId(dataFieldDTO.getTenantId())
                            .build();
                //有则返回，无则新建
                StandardGroupDTO standardGroup = importUtil.getStandardGroup(standardGroupDTO);
                dataFieldDTO.setGroupId(standardGroup.getGroupId());
                importDataFieldDTOList.add(dataFieldDTO);
                dataFieldDTO.setStandardStatus(CREATE);
            }
        }
        batchInsertDTOSelective(importDataFieldDTOList);
        return true;
    }

    @Override
    public List<DataFieldDTO> list(DataFieldDTO dataFieldDTO) {
        List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(dataFieldDTO);
        if(DataSecurityHelper.isTenantOpen()){
            for(DataFieldDTO tmp : dataFieldDTOList){
                tmp.setChargeName(DataSecurityHelper.decrypt(tmp.getChargeName()));
                tmp.setChargeEmail(DataSecurityHelper.decrypt(tmp.getChargeEmail()));
                tmp.setChargeTel(DataSecurityHelper.decrypt(tmp.getChargeTel()));
            }
        }
        return dataFieldDTOList;
    }
}