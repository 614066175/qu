package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.app.service.StandardAimService;
import com.hand.hdsp.quality.domain.repository.StandardAimRepository;
import org.springframework.stereotype.Service;

/**
 * <p>标准落标表应用服务默认实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Service
public class StandardAimServiceImpl implements StandardAimService {

    private final StandardAimRepository standardAimRepository;

    public StandardAimServiceImpl(StandardAimRepository standardAimRepository) {
        this.standardAimRepository = standardAimRepository;
    }

    @Override
    public void aim(StandardAimDTO standardAimDTO) {
        //标准落标，存标准落标表,前端选过的字段不能再选
        standardAimRepository.insertDTOSelective(standardAimDTO);
        if ("DATA".equals(standardAimDTO.getStandardType())) {
            //对应生成数据质量标准

        }
    }
}
