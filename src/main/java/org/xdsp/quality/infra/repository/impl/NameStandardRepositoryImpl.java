package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.NameStandardDTO;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.NameStandard;
import org.xdsp.quality.domain.repository.NameStandardRepository;
import org.xdsp.quality.domain.repository.StandardGroupRepository;
import org.xdsp.quality.infra.constant.StandardConstant;
import org.xdsp.quality.infra.mapper.DataStandardMapper;
import org.xdsp.quality.infra.mapper.NameStandardMapper;
import org.xdsp.quality.infra.util.ImportUtil;

import java.util.List;

/**
 * <p>命名标准表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameStandardRepositoryImpl extends BaseRepositoryImpl<NameStandard, NameStandardDTO> implements NameStandardRepository {

    private final NameStandardMapper nameStandardMapper;

    private final DataStandardMapper dataStandardMapper;

    private final StandardGroupRepository standardGroupRepository;

    private final ImportUtil importUtil;

    public NameStandardRepositoryImpl(NameStandardMapper nameStandardMapper, DataStandardMapper dataStandardMapper, StandardGroupRepository standardGroupRepository, ImportUtil importUtil) {
        this.nameStandardMapper = nameStandardMapper;
        this.dataStandardMapper = dataStandardMapper;
        this.standardGroupRepository = standardGroupRepository;
        this.importUtil = importUtil;
    }

    @Override
    public List<NameStandardDTO> list(NameStandardDTO nameStandardDTO) {
        List<NameStandardDTO> nameStandardDTOList = nameStandardMapper.list(nameStandardDTO);
        //解密
        nameStandardDTOList.forEach(nameStandardDto -> {
            if (DataSecurityHelper.isTenantOpen()) {
                //解密邮箱，电话
                if (Strings.isNotEmpty(nameStandardDto.getChargeTel())) {
                    nameStandardDto.setChargeTel(DataSecurityHelper.decrypt(nameStandardDto.getChargeTel()));
                }
                if (Strings.isNotEmpty(nameStandardDto.getChargeEmail())) {
                    nameStandardDto.setChargeEmail(DataSecurityHelper.decrypt(nameStandardDto.getChargeEmail()));
                }
                if (Strings.isNotEmpty(nameStandardDto.getChargeDeptName())) {
                    nameStandardDto.setChargeDeptName(DataSecurityHelper.decrypt(nameStandardDto.getChargeDeptName()));
                }
                if (StringUtils.isNotEmpty(nameStandardDto.getChargeName())) {
                    nameStandardDto.setChargeName(DataSecurityHelper.decrypt(nameStandardDto.getChargeName()));
                }
            }
        });
        return nameStandardDTOList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchImportStandard(List<NameStandardDTO> nameStandardDTOList) {
        if (CollectionUtils.isEmpty(nameStandardDTOList)) {
            throw new CommonException("xsta.err.is_empty");
        }
        nameStandardDTOList.forEach(nameStandardDTO -> {
            //根据责任人姓名 获取目标环境的责任人id
            nameStandardDTO.setChargeDeptId(importUtil.getChargeDeptId(nameStandardDTO.getChargeDeptName(), nameStandardDTO.getTenantId()));
            nameStandardDTO.setChargeId(importUtil.getChargerId(nameStandardDTO.getChargeName(), nameStandardDTO.getTenantId()));

            StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                    .groupCode(nameStandardDTO.getGroupCode())
                    //todo 模板没有分组名称，用分组编码当名称。
                    .groupName(nameStandardDTO.getGroupCode())
                    .groupDesc(nameStandardDTO.getGroupCode())
                    .standardType(StandardConstant.StandardType.NAME)
                    .tenantId(nameStandardDTO.getTenantId())
                    .build();
            //有则返回，无则新建
            StandardGroupDTO standardGroup = importUtil.getStandardGroup(standardGroupDTO);
            nameStandardDTO.setGroupId(standardGroup.getGroupId());

            insertDTOSelective(nameStandardDTO);
        });
    }

    @Override
    public NameStandardDTO detail(Long standardId) {
        NameStandardDTO nameStandardDTO = nameStandardMapper.detail(standardId);
        //判断当前租户是否启用安全加密
        if (DataSecurityHelper.isTenantOpen()) {
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
            if (StringUtils.isNotEmpty(nameStandardDTO.getChargeName())) {
                nameStandardDTO.setChargeName(DataSecurityHelper.decrypt(nameStandardDTO.getChargeName()));
            }
        }
        return nameStandardDTO;
    }
}
