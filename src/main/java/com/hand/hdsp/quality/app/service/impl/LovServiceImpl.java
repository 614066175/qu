package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.api.dto.LovValueVersionDTO;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.app.service.LovService;
import com.hand.hdsp.quality.domain.entity.LovValue;
import com.hand.hdsp.quality.domain.entity.LovVersion;
import com.hand.hdsp.quality.domain.repository.LovRepository;
import com.hand.hdsp.quality.domain.repository.LovValueRepository;
import com.hand.hdsp.quality.domain.repository.LovValueVersionRepository;
import com.hand.hdsp.quality.domain.repository.LovVersionRepository;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final LovValueVersionRepository lovValueVersionRepository;

    public LovServiceImpl(LovRepository lovRepository, LovValueRepository lovValueRepository, LovVersionRepository lovVersionRepository, LovValueVersionRepository lovValueVersionRepository) {
        this.lovRepository = lovRepository;
        this.lovValueRepository = lovValueRepository;
        this.lovVersionRepository = lovVersionRepository;
        this.lovValueVersionRepository = lovValueVersionRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LovDTO lovRelease(Long lovId) {
        //记录当前代码头行数据到版本表
        LovDTO lovDTO = lovRepository.selectDTOByPrimaryKey(lovId);
        //todo 多语言
        if (Objects.isNull(lovDTO)) {
            throw new CommonException("没有对应的头表");
        }
        List<LovValueDTO> lovValueDTOS = lovValueRepository.selectDTOByCondition(Condition.builder(LovValue.class)
                .andWhere(Sqls.custom().andEqualTo(LovValue.FIELD_LOV_ID, lovId))
                .build());

        List<LovVersionDTO> lovVersionDTOS = lovVersionRepository.selectDTOByCondition(Condition.builder(LovVersion.class)
                .andWhere(Sqls.custom().andEqualTo(LovVersion.FIELD_LOV_ID, lovId))
                .orderByDesc(LovVersion.FIELD_LOV_VERSION)
                .build());

        long lovVersion = 1L;
        if (CollectionUtils.isNotEmpty(lovVersionDTOS)) {
            lovVersion = lovVersionDTOS.get(0).getLovVersion() + 1;
        }
        LovVersionDTO lovVersionDTO = new LovVersionDTO();
        BeanUtils.copyProperties(lovDTO, lovVersionDTO);
        lovVersionDTO.setLovVersion(lovVersion);
        //将头表存入版本表
        lovVersionRepository.insertDTOSelective(lovVersionDTO);

        List<LovValueVersionDTO> lovValueVersionDTOList = new ArrayList<>();
        LovValueVersionDTO lovValueVersionDTO;
        for (LovValueDTO lovValue : lovValueDTOS) {
            lovValueVersionDTO = new LovValueVersionDTO();
            BeanUtils.copyProperties(lovValue, lovValueVersionDTO);
            lovValueVersionDTO.setLovVersionId(lovVersionDTO.getLovVersionId());
            lovValueVersionDTOList.add(lovValueVersionDTO);

        }
        lovValueVersionRepository.batchInsertDTOSelective(lovValueVersionDTOList);
        return lovDTO;
    }

    @Override
    public LovDTO detail(Long lovId) {
        //sql形式
        //lovMapper.detail(lovId);
        //查代码集信息
        LovDTO lovDTO = lovRepository.selectDTOByPrimaryKey(lovId);
        //查最大版本
        List<LovVersionDTO> lovVersionDTOS = lovVersionRepository.selectDTOByCondition(Condition.builder(LovVersion.class)
                .andWhere(Sqls.custom().andEqualTo(LovVersion.FIELD_LOV_ID, lovId))
                .orderByDesc(LovVersion.FIELD_LOV_VERSION)
                .build());
        //默认当前版本为1版本
        lovDTO.setLovVersion(1L);
        if (CollectionUtils.isNotEmpty(lovVersionDTOS)) {
            //当前版本时历史版本+1
            lovDTO.setLovVersion(lovVersionDTOS.get(0).getLovVersion() + 1);
        }
        return lovDTO;
    }

}
