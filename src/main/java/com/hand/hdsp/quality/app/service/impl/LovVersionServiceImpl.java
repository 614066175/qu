package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.app.service.LovVersionService;
import com.hand.hdsp.quality.domain.entity.LovVersion;
import com.hand.hdsp.quality.domain.repository.LovVersionRepository;
import com.hand.hdsp.quality.infra.mapper.LovVersionMapper;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>LOV表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LovVersionServiceImpl implements LovVersionService {


    private final LovVersionRepository lovVersionRepository;

    private final LovVersionMapper lovVersionMapper;

    public LovVersionServiceImpl(LovVersionRepository lovVersionRepository, LovVersionMapper lovVersionMapper) {
        this.lovVersionRepository = lovVersionRepository;
        this.lovVersionMapper = lovVersionMapper;
    }

    @Override
    public List<LovVersionDTO> listAll(LovVersionDTO lovVersionDTO) {
        return lovVersionMapper.listAll(lovVersionDTO);
//        return lovVersionRepository.selectDTOByCondition(Condition.builder(LovVersion.class)
//                .andWhere(Sqls.custom().andEqualTo(LovVersion.FIELD_LOV_ID, lovVersionDTO.getLovId()))
//                .orderByAsc(LovVersion.FIELD_LOV_VERSION)
//                .build());
    }


}
