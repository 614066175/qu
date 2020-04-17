package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;
import com.hand.hdsp.quality.infra.dataobject.BatchResultRuleDO;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>批数据方案结果表-规则信息Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchResultRuleMapper extends BaseMapper<BatchResultRule> {

    /**
     * 查询历史实际值
     *
     * @param batchResultRule
     * @return
     */
    List<BatchResultRuleDO> queryList(BatchResultRuleDO batchResultRule);

    /**
     * 根据结果id查询出所有数据
     *
     * @param resultId
     * @return
     */
    List<BatchResultRuleDTO> selectByResultId(Long resultId);

    /**
     *  查出各个告警等级数
     * @param batchResultRule
     * @return
     */
    List<ResultWaringVO> selectWaringLevel(BatchResultRuleDO batchResultRule);
}
