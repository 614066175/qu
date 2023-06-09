package org.xdsp.quality.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchResultItemDTO;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.domain.repository.BatchResultItemRepository;
import org.xdsp.quality.infra.mapper.BatchResultItemMapper;
import org.xdsp.quality.infra.util.JsonUtils;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
//        String datasourceType = SpringDatasourceHelper.getDatasourceType();
//        List<BatchResultItemDTO> content = page.getContent();
//        if("ORACLE".equals(datasourceType)&&Objects.nonNull(batchResultItemDTO.getFieldName())){
//            content.stream().filter(dto -> {
//                List<String> fields = Arrays.asList(dto.getFieldName().split(","));
//                List<String> collect = fields.stream().peek(s -> s.substring(0, s.indexOf("("))).collect(Collectors.toList());
//                return collect.contains(batchResultItemDTO.getFieldName());
//            }).collect(Collectors.toList());
//        }
        //如果字段条件不为空
        for (BatchResultItemDTO dto : page.getContent()) {
            //处理告警配置
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevelJson()));
            //处理告警结果，[{"warningLevel":"RED","levelCount":1},{"warningLevel":"RED","levelCount":1}]
            //一个item可以有多个告警等级结果,用逗号分隔
            if (Strings.isNotEmpty(dto.getWarningLevel())) {
                List<WarningLevelVO> warningLevelVOList = JsonUtils.json2WarningLevelVO(dto.getWarningLevel());
                Set<String> warningLevel = warningLevelVOList.stream().map(WarningLevelVO::getWarningLevel).collect(Collectors.toSet());
                dto.setWarningLevel(StringUtils.join(warningLevel, ","));
            }
        }
        return page;
    }
}
