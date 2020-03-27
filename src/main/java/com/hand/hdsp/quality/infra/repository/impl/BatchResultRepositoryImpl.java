package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class BatchResultRepositoryImpl extends BaseRepositoryImpl<BatchResult, BatchResultDTO> implements BatchResultRepository {

    private final BatchResultMapper batchResultMapper;

    public BatchResultRepositoryImpl(BatchResultMapper batchResultMapper) {
        this.batchResultMapper = batchResultMapper;
    }

    @Override
    public Page<BatchResultDTO> listAll(BatchResultDTO batchResultDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> batchResultMapper.listByGroup(batchResultDTO));
    }

    @Override
    public List<BatchResultDTO> showReport(BatchResultDTO batchResultDTO) {
        return batchResultMapper.showReport(batchResultDTO);
    }
}
