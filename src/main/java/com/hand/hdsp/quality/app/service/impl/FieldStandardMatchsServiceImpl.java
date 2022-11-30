package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.FieldStandardMatchsDTO;
import com.hand.hdsp.quality.app.service.FieldStandardMatchsService;
import com.hand.hdsp.quality.domain.repository.FieldStandardMatchsRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
/**
 * <p>字段标准匹配记录表应用服务默认实现</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
@Service
public class FieldStandardMatchsServiceImpl implements FieldStandardMatchsService {

    private final FieldStandardMatchsRepository fieldStandardMatchsRepository;

    public FieldStandardMatchsServiceImpl(FieldStandardMatchsRepository fieldStandardMatchsRepository) {
        this.fieldStandardMatchsRepository = fieldStandardMatchsRepository;
    }

    /**
     * @param pageRequest 分页条件
     * @param fieldStandardMatchsDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    @Override
    public Page<FieldStandardMatchsDTO> pageAll(PageRequest pageRequest, FieldStandardMatchsDTO fieldStandardMatchsDTO) {
        return fieldStandardMatchsRepository.pageMatches(pageRequest,fieldStandardMatchsDTO);
    }
}
