package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.RootMatchHisDTO;
import com.hand.hdsp.quality.app.service.RootMatchHisService;
import com.hand.hdsp.quality.domain.repository.RootMatchHisRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
/**
 * <p>字段标准匹配记录表应用服务默认实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Service
public class RootMatchHisServiceImpl implements RootMatchHisService {

    private final RootMatchHisRepository rootMatchHisRepository;

    public RootMatchHisServiceImpl(RootMatchHisRepository rootMatchHisRepository) {
        this.rootMatchHisRepository = rootMatchHisRepository;
    }


    /**
     * @param pageRequest 分页参数
     * @param rootMatchHisDTO 查询条件
     * @return 分页结果
     */
    @Override
    public Page<RootMatchHisDTO> pageAll(PageRequest pageRequest, RootMatchHisDTO rootMatchHisDTO) {
        return rootMatchHisRepository.pageMatches(pageRequest,rootMatchHisDTO);
    }
}
