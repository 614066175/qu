package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.StreamingResultRuleDTO;
import org.xdsp.quality.domain.entity.StreamingResultRule;
import org.xdsp.quality.infra.vo.ResultWaringVO;

import java.util.List;

/**
 * <p>实时数据方案结果表-规则信息Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultRuleMapper extends BaseMapper<StreamingResultRule> {

    /**
     * 根据topicInfo查询各种告警等级数
     *
     * @param streamingResultRuleDTO
     * @return
     */
    List<ResultWaringVO> selectWarnByTopic(StreamingResultRuleDTO streamingResultRuleDTO);
}
