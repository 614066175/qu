package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultBaseDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>批数据方案结果表-表信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchResultBaseRepositoryImpl extends BaseRepositoryImpl<BatchResultBase, BatchResultBaseDTO> implements BatchResultBaseRepository {

    private final BatchResultItemMapper batchResultItemMapper;

    public BatchResultBaseRepositoryImpl(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public List<BatchResultBaseDTO> listResultBase(BatchResultBaseDTO batchResultBaseDTO) {
        List<BatchResultBaseDTO> batchResultBaseDTOList = this.selectDTOByCondition(
                Condition.builder(BatchResultBase.class)
                        .where(Sqls.custom()
                                .andEqualTo(BatchResultBase.FIELD_RESULT_ID, batchResultBaseDTO.getResultId(), false)
                                .andEqualTo(BatchResultBase.FIELD_TENANT_ID, batchResultBaseDTO.getTenantId(), false))
                        .build()
        );
        if (CollectionUtils.isNotEmpty(batchResultBaseDTOList)) {
            batchResultBaseDTOList.forEach(s -> s.setResultWaringVOS(batchResultItemMapper.selectWaringLevel(s)));
        }
        return batchResultBaseDTOList;
    }
}
