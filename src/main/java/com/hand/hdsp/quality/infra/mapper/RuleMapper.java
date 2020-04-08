package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>规则表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleMapper extends BaseMapper<Rule> {

    /**
     * 列表
     *
     * @param ruleCodeList 查询条件
     * @return
     */
    List<RuleDTO> list(@Param("ruleCode") List<String> ruleCodeList, @Param("ruleModel") String ruleModel);

    /**
     * 查询所有
     *
     * @param ruleModel 查询条件
     * @return
     */
    List<RuleDTO> listAll(@Param("ruleModel") String ruleModel);
}
