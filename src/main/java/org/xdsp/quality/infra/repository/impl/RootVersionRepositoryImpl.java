package org.xdsp.quality.infra.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.RootVersionDTO;
import org.xdsp.quality.domain.entity.RootVersion;
import org.xdsp.quality.domain.repository.RootVersionRepository;
import org.xdsp.quality.infra.mapper.RootVersionMapper;

import java.util.List;

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
        for (RootVersion temp : list) {
            decodeForRootVersion(temp);
        }
        return list;
    }

    @Override
    public RootVersion detail(Long id) {
        RootVersion result = rootVersionMapper.detail(id);
        decodeForRootVersion(result);
        return result;
    }

    /**
     * 解密
     *
     * @param rootVersion
     */
    private void decodeForRootVersion(RootVersion rootVersion) {
        if (DataSecurityHelper.isTenantOpen()) {
            // 责任部门
            if (StringUtils.isNotEmpty(rootVersion.getChargeDept())) {
                rootVersion.setChargeDept(DataSecurityHelper.decrypt(rootVersion.getChargeDept()));
            }
            // 责任人电话
            if (StringUtils.isNotEmpty(rootVersion.getChargeTel())) {
                rootVersion.setChargeTel(DataSecurityHelper.decrypt(rootVersion.getChargeTel()));
            }
            // 责任人邮箱
            if (StringUtils.isNotEmpty(rootVersion.getChargeEmail())) {
                rootVersion.setChargeEmail(DataSecurityHelper.decrypt(rootVersion.getChargeEmail()));
            }
            // 责任人姓名
            if (StringUtils.isNotEmpty(rootVersion.getChargeName())) {
                rootVersion.setChargeName(DataSecurityHelper.decrypt(rootVersion.getChargeName()));
            }
        }
    }
}
