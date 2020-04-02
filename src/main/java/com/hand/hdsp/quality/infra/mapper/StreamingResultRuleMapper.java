package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.domain.entity.StreamingResultRule;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>实时数据方案结果表-规则信息Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
public interface StreamingResultRuleMapper extends BaseMapper<StreamingResultRule> {

    /**
     * 根据topicInfo查询各种告警等级数
     *
     * @param tenantId
     * @param topicInfo
     * @return ResultWaringVO
     */
    List<ResultWaringVO> selectWarnByTopic(@Param("tenantId") Long tenantId,@Param("topicInfo") String topicInfo);
}
