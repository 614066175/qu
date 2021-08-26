package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.SuggestDTO;
import com.hand.hdsp.quality.app.service.SuggestService;
import com.hand.hdsp.quality.domain.repository.BatchResultRepository;
import com.hand.hdsp.quality.domain.repository.SuggestRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.mapper.SuggestMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>问题知识库表应用服务默认实现</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Service
public class SuggestServiceImpl implements SuggestService {

    private final SuggestMapper suggestMapper;
    private final SuggestRepository suggestRepository;
    private final BatchResultRepository batchResultRepository;
    private final String LEFT_BRACKETS = "(";

    public SuggestServiceImpl(SuggestMapper suggestMapper, SuggestRepository suggestRepository, BatchResultRepository batchResultRepository) {
        this.suggestMapper = suggestMapper;
        this.suggestRepository = suggestRepository;
        this.batchResultRepository = batchResultRepository;
    }

    @Override
    public SuggestDTO createSuggest(SuggestDTO suggestDTO) {
        List<SuggestDTO> suggests = suggestMapper.selectRuleId(suggestDTO);
        if (CollectionUtils.isEmpty(suggests) || suggests.size() > 1) {
            throw new CommonException(ErrorCode.CHECK_RULE_IS_ERROR);
        }
        suggestDTO.setRuleId(suggests.get(0).getRuleId());
        suggestRepository.insertDTOSelective(suggestDTO);
        return suggestDTO;
    }

    @Override
    public Page<SuggestDTO> list(PageRequest pageRequest, SuggestDTO suggestDTO) {
        if(suggestDTO.getProblemId() == null){
            //前端取消选中需要
            return new Page<>();
        }
        //问题建议使用该接口时，传递problemId为-1
        if(suggestDTO.getProblemId() == -1){
            suggestDTO.setProblemId(null);
        }
        //如果前端传递的字段类型字符串包含 "("  则做类型转换
        if(StringUtils.isNotBlank(suggestDTO.getColumnType())){
            if(suggestDTO.getColumnType().contains(LEFT_BRACKETS)){
                suggestDTO.setTypes(batchResultRepository
                        .typeConvert(suggestDTO.getColumnType()));
            } else {
                List<String> list = new ArrayList<>();
                list.add(suggestDTO.getColumnType());
                suggestDTO.setTypes(list);
            }
        }
        return PageHelper.doPageAndSort(pageRequest, () ->suggestMapper.list(suggestDTO));
    }

}
