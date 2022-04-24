package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LocValueDTO;
import com.hand.hdsp.quality.app.service.LocValueService;
import com.hand.hdsp.quality.domain.repository.LocValueRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>loc独立值集表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LocValueServiceImpl implements LocValueService {
    private LocValueRepository locValueRepository;

    public LocValueServiceImpl(LocValueRepository locValueRepository) {
        this.locValueRepository = locValueRepository;
    }

    @Override
    public Page<LocValueDTO> list(PageRequest pageRequest, LocValueDTO locValueDTO) {


        return PageHelper.doPageAndSort(pageRequest,()->locValueRepository.queryList(locValueDTO)) ;
    }
}
