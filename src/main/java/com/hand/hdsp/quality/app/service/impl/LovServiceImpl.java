package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.app.service.LovService;
import com.hand.hdsp.quality.domain.entity.Lov;
import com.hand.hdsp.quality.domain.entity.LovValue;
import com.hand.hdsp.quality.domain.entity.LovVersion;
import com.hand.hdsp.quality.domain.repository.LovRepository;
import com.hand.hdsp.quality.domain.repository.LovValueRepository;
import com.hand.hdsp.quality.domain.repository.LovVersionRepository;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>LOV表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LovServiceImpl implements LovService {
    private final LovRepository lovRepository;

    private final LovValueRepository lovValueRepository;

    private final LovVersionRepository lovVersionRepository;

    public LovServiceImpl(LovRepository lovRepository, LovValueRepository lovValueRepository, LovVersionRepository lovVersionRepository) {
        this.lovRepository = lovRepository;
        this.lovValueRepository = lovValueRepository;
        this.lovVersionRepository = lovVersionRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LovDTO lovRelease(Long lovId) {
        //记录当前代码头行数据到版本表
        LovDTO lovDTO = lovRepository.selectDTOByPrimaryKey(lovId);
        if (lovDTO == null) {
            throw new CommonException("");
        }
        List<LovValueDTO> lovValueDTOS = lovValueRepository.selectDTOByCondition(Condition.builder(LovValue.class)
                .andWhere(Sqls.custom().andEqualTo(Lov.FIELD_LOV_ID, lovId))
                .build());

        List<LovVersionDTO> lovVersionDTOS = lovVersionRepository.selectDTOByCondition(Condition.builder(LovVersion.class)
                .andWhere(Sqls.custom().andEqualTo(LovVersion.FIELD_LOV_ID, lovId))
                .orderByDesc(LovVersion.FIELD_LOV_VERSION)
                .build());
        Long lovVersion = 1L;
        if (CollectionUtils.isNotEmpty(lovVersionDTOS)) {
            lovVersion = lovVersionDTOS.get(0).getLovVersion();
        }
        LovVersionDTO lovVersionDTO = new LovVersionDTO();
        BeanUtils.copyProperties(lovDTO, lovVersionDTO);
        lovVersionDTO.setLovVersion(lovVersion);
        lovVersionRepository.insertDTOSelective(lovVersionDTO);

        //todo 值版本
        return lovDTO;
    }
}
