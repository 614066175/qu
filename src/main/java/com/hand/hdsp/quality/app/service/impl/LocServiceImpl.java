package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LocDTO;
import com.hand.hdsp.quality.api.dto.LocValueDTO;
import com.hand.hdsp.quality.api.dto.LocValueVersionDTO;
import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.app.service.LocService;
import com.hand.hdsp.quality.domain.entity.LocValue;
import com.hand.hdsp.quality.domain.entity.LocVersion;
import com.hand.hdsp.quality.domain.repository.LocRepository;
import com.hand.hdsp.quality.domain.repository.LocValueRepository;
import com.hand.hdsp.quality.domain.repository.LocValueVersionRepository;
import com.hand.hdsp.quality.domain.repository.LocVersionRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.mapper.LocMapper;
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
 * <p>loc表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LocServiceImpl implements LocService {
    private final LocRepository locRepository;

    private final LocValueRepository locValueRepository;

    private final LocVersionRepository locVersionRepository;

    private final LocValueVersionRepository locValueVersionRepository;

    private final LocMapper locMapper;

    public LocServiceImpl(LocRepository locRepository, LocValueRepository locValueRepository, LocVersionRepository locVersionRepository, LocValueVersionRepository locValueVersionRepository, LocMapper locMapper) {
        this.locRepository = locRepository;
        this.locValueRepository = locValueRepository;
        this.locVersionRepository = locVersionRepository;
        this.locValueVersionRepository = locValueVersionRepository;
        this.locMapper = locMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LocDTO locRelease(Long locId) {
        //记录当前代码头行数据到版本表
        LocDTO locDTO = locRepository.selectDTOByPrimaryKey(locId);
        if (Objects.isNull(locDTO)) {
            throw new CommonException(ErrorCode.LOC_NO_FILE);
        }
        List<LocValueDTO> locValueDTOS = locValueRepository.selectDTOByCondition(Condition.builder(LocValue.class)
                .andWhere(Sqls.custom().andEqualTo(LocValue.FIELD_LOC_ID, locId))
                .build());

        List<LocVersionDTO> locVersionDTOS = locVersionRepository.selectDTOByCondition(Condition.builder(LocVersion.class)
                .andWhere(Sqls.custom().andEqualTo(LocVersion.FIELD_LOC_ID, locId))
                .orderByDesc(LocVersion.FIELD_LOC_VERSION)
                .build());

        long locVersion = 1L;
        if (CollectionUtils.isNotEmpty(locVersionDTOS)) {
            locVersion = locVersionDTOS.get(0).getLocVersion() + 1;
        }
        LocVersionDTO locVersionDTO = new LocVersionDTO();
        BeanUtils.copyProperties(locDTO, locVersionDTO);
        locVersionDTO.setLocVersion(locVersion);
        //将头表存入版本表
        locVersionRepository.insertDTOSelective(locVersionDTO);

        List<LocValueVersionDTO> locValueVersionDTOList = new ArrayList<>();
        LocValueVersionDTO locValueVersionDTO;
        for (LocValueDTO locValue : locValueDTOS) {

            locValueVersionDTO = new LocValueVersionDTO();
            BeanUtils.copyProperties(locValue, locValueVersionDTO);
            locValueVersionDTO.setLocVersionId(locVersionDTO.getLocVersionId());
            locValueVersionDTOList.add(locValueVersionDTO);
        }
        locValueVersionRepository.batchInsertDTOSelective(locValueVersionDTOList);
        return locDTO;
    }

    @Override
    public LocDTO detail(Long locId) {
        //sql形式
        //locMapper.detail(locId);

        //查代码集信息
        LocDTO locDTO = locRepository.selectDTOByPrimaryKey(locId);
        //查最大版本
        List<LocVersionDTO> locVersionDTOS = locVersionRepository.selectDTOByCondition(Condition.builder(LocVersion.class)
                .andWhere(Sqls.custom().andEqualTo(LocVersion.FIELD_LOC_ID, locId))
                .orderByDesc(LocVersion.FIELD_LOC_VERSION)
                .build());
        //默认当前版本为1版本
        locDTO.setLocVersion(1L);
        if (CollectionUtils.isNotEmpty(locVersionDTOS)) {
            //当前版本时历史版本+1
            locDTO.setLocVersion(locVersionDTOS.get(0).getLocVersion() + 1);
        }
        return locDTO;
    }

    @Override
    public List<LocDTO> listAll(LocDTO locDTO) {
        return locMapper.list(locDTO);
    }
}
