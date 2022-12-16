package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.RootMatchHis;
import com.hand.hdsp.quality.api.dto.RootMatchHisDTO;
import com.hand.hdsp.quality.domain.repository.RootMatchHisRepository;
import com.hand.hdsp.quality.infra.mapper.RootMatchHisMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>字段标准匹配记录表资源库实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Component
public class RootMatchHisRepositoryImpl extends BaseRepositoryImpl<RootMatchHis, RootMatchHisDTO> implements RootMatchHisRepository {

    private final RootMatchHisMapper rootMatchHisMapper;

    public RootMatchHisRepositoryImpl(RootMatchHisMapper rootMatchHisMapper) {
        this.rootMatchHisMapper = rootMatchHisMapper;
    }


    /**
     * @param pageRequest     分页参数
     * @param rootMatchHisDTO 查询条件
     * @return 分页结果
     */
    @Override
    public Page<RootMatchHisDTO> pageMatches(PageRequest pageRequest, RootMatchHisDTO rootMatchHisDTO) {
        return PageHelper.doPageAndSort(pageRequest, () ->rootMatchHisMapper.listAll(rootMatchHisDTO));
    }
}
