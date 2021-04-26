package com.hand.hdsp.quality.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.BatchResultItemDTO;
import com.hand.hdsp.quality.app.service.BatchResultItemService;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/**
 * <p>批数据方案结果表-校验项信息应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Service
public class BatchResultItemServiceImpl implements BatchResultItemService {
    private final BatchResultItemMapper batchResultItemMapper;

    public BatchResultItemServiceImpl(BatchResultItemMapper batchResultItemMapper) {
        this.batchResultItemMapper = batchResultItemMapper;
    }


    @Override
    public Map<String, Map<String, Long>> analysisQuality(BatchResultItemDTO batchResultItemDTO) {
        Map map = new HashMap();
        //获取表的所有item
        List<BatchResultItemDTO> batchResultItemDTOS = batchResultItemMapper.assetTable(batchResultItemDTO);
        //获取告警等级的结果
        //处理告警结果
        List<WarningLevelVO> warningLevelResults = new ArrayList<>();
        batchResultItemDTOS.forEach(dto -> {
            if (Strings.isNotEmpty(dto.getWarningLevel())) {
                dto.setWarningLevelResult(JsonUtils.json2WarningLevelVO(dto.getWarningLevel()));
                CollectionUtils.addAll(warningLevelResults, dto.getWarningLevelResult());
            }
        });

        Map<String, List<WarningLevelVO>> result = warningLevelResults
                .stream()
                .collect(Collectors.groupingBy(WarningLevelVO::getWarningLevel));

        //告警等级结果map
        Map<String, Long> warningLevelMap = new HashMap<>();

        result.keySet().forEach(key -> warningLevelMap.put(key, Long.parseLong(String.valueOf(result.get(key).size()))));
        map.put("warningLevelMap", warningLevelMap);


        //规则异常数量map
        Map<String, Long> ruleLevelCountMap = new HashMap<>();

        //todo 是否按照ruleCode分类，ruleName可能重复
        //按照规则名称分类
        Map<String, List<BatchResultItemDTO>> batchResultItemMap = batchResultItemDTOS.stream().collect(Collectors.groupingBy(BatchResultItemDTO::getRuleName));

        batchResultItemMap.keySet().forEach(key -> {
            List<BatchResultItemDTO> itemDTOList = batchResultItemMap.get(key);
            //过滤正常结果，和非字段规则
            long sum = itemDTOList.stream()
                    .filter(dto -> CollectionUtils.isNotEmpty(dto.getWarningLevelResult()))
                    .filter(dto -> "FIELD".equals(dto.getRuleType()))
                    .mapToLong(dto -> {
                        List<WarningLevelVO> warningLevelResult = dto.getWarningLevelResult();
                        return warningLevelResult.stream().mapToLong(WarningLevelVO::getLevelCount).sum();
                    }).sum();
            ruleLevelCountMap.put(key, sum);
        });
        map.put("ruleLevelCountMap", ruleLevelCountMap);
        return map;
    }
}
