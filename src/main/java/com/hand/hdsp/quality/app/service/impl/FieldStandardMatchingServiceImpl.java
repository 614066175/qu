package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;
import com.hand.hdsp.quality.app.service.FieldStandardMatchingService;
import com.hand.hdsp.quality.domain.repository.FieldStandardMatchingRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
/**
 * <p>字段标准匹配表应用服务默认实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@Service
public class FieldStandardMatchingServiceImpl implements FieldStandardMatchingService {

    private final FieldStandardMatchingRepository fieldStandardMatchingRepository;

    public FieldStandardMatchingServiceImpl(FieldStandardMatchingRepository fieldStandardMatchingRepository) {
        this.fieldStandardMatchingRepository = fieldStandardMatchingRepository;
    }

    /**
     * @param pageRequest 分页条件
     * @param fieldStandardMatchingDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    @Override
    public Page<FieldStandardMatchingDTO> pageFieldStandardMatching(PageRequest pageRequest, FieldStandardMatchingDTO fieldStandardMatchingDTO) {
        return fieldStandardMatchingRepository.pageFieldStandardMatching(pageRequest,fieldStandardMatchingDTO);
    }
}
