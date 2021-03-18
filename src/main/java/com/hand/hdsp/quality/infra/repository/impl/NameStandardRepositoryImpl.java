package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;
import java.util.Objects;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameStandardRepositoryImpl extends BaseRepositoryImpl<NameStandard, NameStandardDTO> implements NameStandardRepository {

    private final NameStandardMapper nameStandardMapper;

    private final DataStandardMapper dataStandardMapper;

    public NameStandardRepositoryImpl(NameStandardMapper nameStandardMapper, DataStandardMapper dataStandardMapper) {
        this.nameStandardMapper = nameStandardMapper;
        this.dataStandardMapper = dataStandardMapper;
    }

    @Override
    public List<NameStandardDTO> list(NameStandardDTO nameStandardDTO) {
        return nameStandardMapper.list(nameStandardDTO);
    }

    @Override
    public void importStandard(NameStandardDTO nameStandardDTO) {
        List<NameStandardDTO> nameStandardDTOList = this.selectDTOByCondition(Condition
                .builder(NameStandard.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(NameStandard.FIELD_STANDARD_CODE,nameStandardDTO.getStandardCode())
                        .andEqualTo(NameStandard.FIELD_TENANT_ID,nameStandardDTO.getTenantId()))
                .build());
        if (!CollectionUtils.isEmpty(nameStandardDTOList)){
            return;
        }
        Long groupId = nameStandardMapper.getGroupId(nameStandardDTO.getGroupCode());
        if(!Objects.isNull(groupId)){
            nameStandardDTO.setGroupId(groupId);
            this.insertDTOSelective(nameStandardDTO);
        }
    }

    @Override
    public void batchImportStandard(List<NameStandardDTO> nameStandardDTOList) {
        if (CollectionUtils.isEmpty(nameStandardDTOList)){
            throw new CommonException("hdsp.xsta.err.is_empty");
        }
        nameStandardDTOList.forEach(this::importStandard);
    }

    @Override
    public NameStandardDTO detail(Long standardId) {
        NameStandardDTO nameStandardDTO = nameStandardMapper.detail(standardId);
        //判断当前租户是否启用安全加密
        if(dataStandardMapper.isEncrypt(nameStandardDTO.getTenantId())==1){
            //解密邮箱，电话
            if (Strings.isNotEmpty(nameStandardDTO.getChargeTel())) {
                nameStandardDTO.setChargeTel(DataSecurityHelper.decrypt(nameStandardDTO.getChargeTel()));
            }
            if (Strings.isNotEmpty(nameStandardDTO.getChargeEmail())) {
                nameStandardDTO.setChargeEmail(DataSecurityHelper.decrypt(nameStandardDTO.getChargeEmail()));
            }
            if (Strings.isNotEmpty(nameStandardDTO.getChargeDeptName())) {
                nameStandardDTO.setChargeDeptName(DataSecurityHelper.decrypt(nameStandardDTO.getChargeDeptName()));
            }
        }
        return nameStandardDTO;
    }
}
