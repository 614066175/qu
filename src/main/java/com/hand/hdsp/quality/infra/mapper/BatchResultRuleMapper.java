package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;
import com.hand.hdsp.quality.infra.dataobject.BatchResultRuleDO;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>批数据方案结果表-规则信息Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchResultRuleMapper extends BaseMapper<BatchResultRule> {

    List<BatchResultRuleDO> queryList(BatchResultRuleDO batchResultRule);

    /**
     * 根据结果id查询出所有数据
     *
     * @param resultId
     * @return
     */
    List<BatchResultRuleDTO> selectByResultId(Long resultId);

    /**
     * 根据表名查询各个告警等级数
     *
     * @param tenantId
     * @param tableName
     * @return ResultWaringVO
     */
    List<ResultWaringVO> selectWaringLevel(@Param("tenantId") Long tenantId,@Param("tableName") String tableName);
}
