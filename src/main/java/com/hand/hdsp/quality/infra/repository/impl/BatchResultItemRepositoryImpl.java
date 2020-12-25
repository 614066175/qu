package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.repository.BatchResultItemRepository;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案结果表-校验项信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Component
public class BatchResultItemRepositoryImpl extends BaseRepositoryImpl<BatchResultItem, BatchResultItemDTO> implements BatchResultItemRepository {

    private final BatchResultItemMapper batchResultItemMapper;

    public BatchResultItemRepositoryImpl(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }

    @Override
    public List<BatchResultItemDTO> selectByResultId(Long resultId) {
        return batchResultItemMapper.selectByResultId(resultId);
    }


    @Override
    public List<String> selectWaringLevelByResultId(Long resultId) {
        return batchResultItemMapper.selectWaringLevelByResultId(resultId);
    }

    @Override
    public Page<BatchResultItemDTO> listRuleError(PageRequest pageRequest, BatchResultItemDTO batchResultItemDTO) {
        Page<BatchResultItemDTO> page = PageHelper.doPage(pageRequest, () -> batchResultItemMapper.listRuleError(batchResultItemDTO));
        for (BatchResultItemDTO dto : page.getContent()) {
            //设置告警结果
            dto.setWarningLevelResult(JsonUtils.json2WarningLevelVO(dto.getWarningLevel()));
            //设置方案告警配置
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevelJson()));
        }
        return page;
    }

    @Override
    public Page<BatchResultItemDTO> assetTable(PageRequest pageRequest, BatchResultItemDTO batchResultItemDTO) {
        Page<BatchResultItemDTO> page = PageHelper.doPage(pageRequest, () -> batchResultItemMapper.assetTable(batchResultItemDTO));
        for (BatchResultItemDTO dto : page.getContent()) {
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevelJson()));
        }
        return page;
    }
}
