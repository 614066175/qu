package org.xdsp.quality.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.xdsp.quality.api.dto.StandardExtraDTO;
import org.xdsp.quality.app.service.StandardExtraService;
import org.xdsp.quality.domain.entity.StandardExtra;
import org.xdsp.quality.domain.repository.StandardExtraRepository;
import org.xdsp.quality.infra.constant.ErrorCode;

import java.util.List;

/**
 * <p>标准附加信息表应用服务默认实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Service
public class StandardExtraServiceImpl implements StandardExtraService {

    private final StandardExtraRepository standardExtraRepository;

    public StandardExtraServiceImpl(StandardExtraRepository standardExtraRepository) {
        this.standardExtraRepository = standardExtraRepository;
    }



    @Override
    public void batchUpdate(List<StandardExtraDTO> standardExtraDTOList) {
        if(CollectionUtils.isEmpty(standardExtraDTOList)){
            throw new CommonException(ErrorCode.STANDARD_EXTRA_LIST_IS_EMPTY);
        }
        standardExtraDTOList.forEach(standardExtraDTO -> {
            //判断是否存在，不存在则插入，存在则更新
            List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(StandardExtra.FIELD_STANDARD_ID, standardExtraDTO.getStandardId())
                            .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, standardExtraDTO.getStandardType())
                            .andEqualTo(StandardExtra.FIELD_EXTRA_KEY, standardExtraDTO.getExtraKey())
                            .andEqualTo(StandardExtra.FIELD_PROJECT_ID,standardExtraDTO.getProjectId())
                            .andEqualTo(StandardExtra.FIELD_TENANT_ID, standardExtraDTO.getTenantId()))
                    .build());
            if(CollectionUtils.isNotEmpty(standardExtraDTOS)){
                standardExtraDTO.setStandardId(standardExtraDTOS.get(0).getStandardId());
                standardExtraRepository.updateByDTOPrimaryKey(standardExtraDTO);
            }else{
                standardExtraRepository.insertDTOSelective(standardExtraDTO);
            }
        });
    }

}
