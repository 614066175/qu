package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.DATA;

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
            throw new CommonException("责任人不存在");
        }
        return chargeId.get(0);
    }

    public Long getChargeDeptId(String chargeDeptName, Long tenantId) {
        if (DataSecurityHelper.isTenantOpen()) {
            chargeDeptName = DataSecurityHelper.encrypt(chargeDeptName);
        }
        List<Long> chargeDeptId = dataStandardMapper.selectIdByChargeDeptName(chargeDeptName, tenantId);
        if (CollectionUtils.isEmpty(chargeDeptId)) {
            throw new CommonException("责任部门不存在");
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
