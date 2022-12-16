package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.RootMatch;
import com.hand.hdsp.quality.api.dto.RootMatchDTO;
import com.hand.hdsp.quality.domain.repository.RootMatchRepository;
import com.hand.hdsp.quality.infra.mapper.RootMatchMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>字段标准匹配表资源库实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Component
public class RootMatchRepositoryImpl extends BaseRepositoryImpl<RootMatch, RootMatchDTO> implements RootMatchRepository {

    private final RootMatchMapper rootMatchMapper;

    public RootMatchRepositoryImpl(RootMatchMapper rootMatchMapper) {
        this.rootMatchMapper = rootMatchMapper;
    }

    /**
     * @param pageRequest  分页条件
     * @param rootMatchDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    @Override
    public Page<RootMatchDTO> pageRootMatchDTOList(PageRequest pageRequest, RootMatchDTO rootMatchDTO) {
        return PageHelper.doPageAndSort(pageRequest,() -> rootMatchMapper.findAllMatch(rootMatchDTO));
    }
}
