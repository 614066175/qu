package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.api.dto.LovValueVersionDTO;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.app.service.LovService;
import com.hand.hdsp.quality.domain.entity.Lov;
import com.hand.hdsp.quality.domain.entity.LovValue;
import com.hand.hdsp.quality.domain.entity.LovValueVersion;
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
        if (lovDTO == null) {
            throw new CommonException("没有对应的头表");
        }
        List<LovValueDTO> lovValueDTOS = lovValueRepository.selectDTOByCondition(Condition.builder(LovValue.class)
                .andWhere(Sqls.custom().andEqualTo(LovValue.FIELD_LOV_ID, lovId))
                .build());
        if(lovValueDTOS==null){
            throw new CommonException("没有对应的行表");
        }

        List<LovVersionDTO> lovVersionDTOS = lovVersionRepository.selectDTOByCondition(Condition.builder(LovVersion.class)
                .andWhere(Sqls.custom().andEqualTo(LovVersion.FIELD_LOV_ID, lovId))
                .orderByDesc(LovVersion.FIELD_LOV_VERSION)
                .build());
        if(lovVersionDTOS==null){
            throw new CommonException("没有对应的版本头标");
        }
//        List<LovValueVersionDTO> lovValueVersionDTOS = lovValueVersionRepository.selectDTOByCondition(Condition.builder(LovValueVersion.class)
//                .andWhere(Sqls.custom().andEqualTo(LovValueVersion.FIELD_LOV_ID, lovId))
//                .orderByDesc(LovValueVersion.FIELD_LOV_VERSION_ID)
//                .build());
//        if(lovVersionDTOS==null){
//            throw new CommonException("没有对应的版本行表");
//        }

        Long lovVersion = 1L;
        if (CollectionUtils.isNotEmpty(lovVersionDTOS)) {
            lovVersion = lovVersionDTOS.get(0).getLovVersion();
        }
        LovVersionDTO lovVersionDTO = new LovVersionDTO();
        BeanUtils.copyProperties(lovDTO, lovVersionDTO);
        lovVersionDTO.setLovVersion(lovVersion);
        //将头表存入版本表
        lovVersionRepository.insertDTOSelective(lovVersionDTO);

        //todo 值版本
        Long finalLovVersion = lovVersion;
        lovValueDTOS.forEach(lovValue->{
            LovValueVersionDTO lovValueVersionDTO = new LovValueVersionDTO();
            BeanUtils.copyProperties(lovValue,lovValueVersionDTO);
            lovValueVersionDTO.setLovVersionId(finalLovVersion);
            lovValueVersionRepository.insertDTOSelective(lovValueVersionDTO);
        });
        return lovDTO;
    }

    @Override
    public void AssertOpen(Long lovId) {
        LovDTO lovDTO = lovRepository.selectDTOByPrimaryKey(lovId);
        lovDTO.setEnabledFlag(0);
    }


}
