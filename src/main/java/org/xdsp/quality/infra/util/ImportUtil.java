package org.xdsp.quality.infra.util;

import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.StandardGroup;
import org.xdsp.quality.domain.repository.StandardGroupRepository;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.mapper.DataStandardMapper;

import java.util.List;

/**
 * <p>
 * 通用导入工具类
 * </p>
 *
 * @author lgl 2022/4/8 15:35
 * @since 1.0
 */
@Component
@Slf4j
public class ImportUtil {

    private final DataStandardMapper dataStandardMapper;

    private final StandardGroupRepository standardGroupRepository;

    public ImportUtil(DataStandardMapper dataStandardMapper, StandardGroupRepository standardGroupRepository) {
        this.dataStandardMapper = dataStandardMapper;
        this.standardGroupRepository = standardGroupRepository;
    }

    public Long getChargerId(String chargerName, Long tenantId) {
        List<Long> chargeId = dataStandardMapper.selectIdByChargeName(chargerName, tenantId);
        if (CollectionUtils.isEmpty(chargeId)) {
            throw new CommonException(ErrorCode.CHARGER_NOT_EXIST);
        }
        return chargeId.get(0);
    }

    public Long getChargeDeptId(String chargeDeptName, Long tenantId) {
        //责任部门可为空
        if(StringUtils.isEmpty(chargeDeptName)){
            return null;
        }
        if (DataSecurityHelper.isTenantOpen()) {
            chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
        }
        List<Long> chargeDeptId = dataStandardMapper.selectIdByChargeDeptName(chargeDeptName, tenantId);
        if (CollectionUtils.isEmpty(chargeDeptId)) {
            throw new CommonException(ErrorCode.CHARGER_DEPT_NOT_EXIST);
        }
        return chargeDeptId.get(0);
    }


    public StandardGroupDTO getStandardGroup(StandardGroupDTO standardGroupDTO) {
        List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, standardGroupDTO.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, standardGroupDTO.getTenantId())
                        .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, standardGroupDTO.getStandardType()))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOS)) {
            return standardGroupDTOS.get(0);
        } else {
            //创建分组
            standardGroupRepository.insertDTOSelective(standardGroupDTO);
            return standardGroupDTO;
        }
    }
}
