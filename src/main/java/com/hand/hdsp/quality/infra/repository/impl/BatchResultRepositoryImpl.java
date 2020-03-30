package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultDTO;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.domain.repository.BatchResultRuleRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class BatchResultRepositoryImpl extends BaseRepositoryImpl<BatchResult, BatchResultDTO> implements BatchResultRepository {

    private final BatchResultMapper batchResultMapper;
    private final BatchPlanRepository batchPlanRepository;
    private final BatchResultBaseRepository batchResultBaseRepository;
    private final BatchResultRuleRepository batchResultRuleRepository;

    public BatchResultRepositoryImpl(BatchResultMapper batchResultMapper, BatchPlanRepository batchPlanRepository, BatchResultBaseRepository batchResultBaseRepository, BatchResultRuleRepository batchResultRuleRepository) {
        this.batchResultMapper = batchResultMapper;
        this.batchPlanRepository = batchPlanRepository;
        this.batchResultBaseRepository = batchResultBaseRepository;
        this.batchResultRuleRepository = batchResultRuleRepository;
    }

    @Override
    public Page<BatchResultDTO> listAll(BatchResultDTO batchResultDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> batchResultMapper.listByGroup(batchResultDTO));
    }

    @Override
    public BatchResultDTO showReport(BatchResultDTO batchResultDTO) {
        BatchResultDTO batchResultDTOs = this.selectDTOByCondition(
                Condition.builder(BatchResult.class)
                        .where(Sqls.custom()
                                .andEqualTo(BatchResult.FIELD_PLAN_ID, batchResultDTO.getPlanId(), true)
                                .andEqualTo(BatchResult.FIELD_TENANT_ID, batchResultDTO.getTenantId(), true))
                        .build()
        ).get(0);
        batchResultDTOs.setPlanName(batchPlanRepository.selectByPrimaryKey(batchResultDTO.getPlanId()).getPlanName());
        List<BatchResultBase> batchResultBases = batchResultBaseRepository.selectByCondition(
                Condition.builder(BatchResultBase.class)
                        .where(Sqls.custom()
                                .andEqualTo(BatchResultBase.FIELD_RESULT_ID, batchResultDTOs.getResultId(), true))
                        .build()
        );
        List<Long> resultBaseIds = batchResultBases.stream().map(BatchResultBase::getResultBaseId).collect(Collectors.toList());
        List<BatchResultRule> batchResultRules = batchResultRuleRepository.selectByCondition(
                Condition.builder(BatchResultRule.class)
                        .where(Sqls.custom()
                                .andIn(BatchResultRule.FIELD_RESULT_BASE_ID, resultBaseIds))
                        .build()
        );
        batchResultDTOs.setBatchResultBases(batchResultBases);
        batchResultDTOs.setBatchResultRules(batchResultRules);
        return batchResultDTOs;
    }
}
