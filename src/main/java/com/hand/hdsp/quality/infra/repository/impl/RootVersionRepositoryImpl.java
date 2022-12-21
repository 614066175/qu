package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.core.util.DataSecurityUtil;
import com.hand.hdsp.quality.api.dto.RootVersionDTO;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.entity.RootVersion;
import com.hand.hdsp.quality.domain.repository.RootVersionRepository;
import com.hand.hdsp.quality.infra.mapper.RootVersionMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * 词根版本 资源库实现
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@Component
public class RootVersionRepositoryImpl extends BaseRepositoryImpl<RootVersion, RootVersionDTO> implements RootVersionRepository {

    private final RootVersionMapper rootVersionMapper;

    public RootVersionRepositoryImpl(RootVersionMapper rootVersionMapper) {
        this.rootVersionMapper = rootVersionMapper;
    }


    @Override
    public List<RootVersion> list(RootVersion rootVersion) {
        List<RootVersion> list = rootVersionMapper.list(rootVersion);
        if(DataSecurityHelper.isTenantOpen()){
            for(RootVersion tmp:list){
                tmp.setChargeDept(DataSecurityUtil.decrypt(tmp.getChargeDept()));
                tmp.setChargeTel(DataSecurityUtil.decrypt(tmp.getChargeTel()));
                tmp.setChargeEmail(DataSecurityUtil.decrypt(tmp.getChargeEmail()));
                tmp.setChargeName(DataSecurityUtil.decrypt(tmp.getChargeName()));
            }
        }
        return list;
    }

    @Override
    public RootVersion detail(Long id) {
        RootVersion result = rootVersionMapper.detail(id);
        if(DataSecurityHelper.isTenantOpen()){
            result.setChargeDept(DataSecurityUtil.decrypt(result.getChargeDept()));
            result.setChargeTel(DataSecurityUtil.decrypt(result.getChargeTel()));
            result.setChargeEmail(DataSecurityUtil.decrypt(result.getChargeEmail()));
            result.setChargeName(DataSecurityUtil.decrypt(result.getChargeName()));
        }
        return result;
    }
}
